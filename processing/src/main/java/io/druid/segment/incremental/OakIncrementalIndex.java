package io.druid.segment.incremental;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import io.druid.data.input.InputRow;
import io.druid.data.input.MapBasedRow;
import io.druid.data.input.Row;
import io.druid.query.aggregation.AggregatorFactory;
import io.druid.query.aggregation.BufferAggregator;
import io.druid.query.aggregation.PostAggregator;
import io.druid.segment.ColumnSelectorFactory;
import io.druid.segment.DimensionHandler;
import io.druid.segment.DimensionIndexer;
import io.druid.segment.incremental.oak.Oak;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dbasin on 6/11/17.
 */
public class OakIncrementalIndex extends IncrementalIndex<BufferAggregator> {
    /**
     * Setting deserializeComplexMetrics to false is necessary for intermediate aggregation such as groupBy that
     * should not deserialize input columns using ComplexMetricSerde for aggregators that return complex metrics.
     *
     * @param incrementalIndexSchema    the schema to use for incremental index
     * @param deserializeComplexMetrics flag whether or not to call ComplexMetricExtractor.extractValue() on the input
     *                                  value for aggregators that return metrics other than float.
     * @param reportParseExceptions     flag whether or not to report ParseExceptions that occur while extracting values
     */
    private FactsHolder factsHolder;
    private Oak<TimeAndDims> rows;

    private volatile Map<String, ColumnSelectorFactory> selectors;
    //given a ByteBuffer and an offset where all aggregates for a row are stored
    //offset + aggOffsetInBuffer[i] would give position in ByteBuffer where ith aggregate
    //is stored
    private volatile int[] aggOffsetInBuffer;


    public OakIncrementalIndex(IncrementalIndexSchema incrementalIndexSchema, boolean deserializeComplexMetrics, boolean reportParseExceptions) {
        super(incrementalIndexSchema, deserializeComplexMetrics, reportParseExceptions);

        factsHolder = new OakFactsHolder();
    }


    class OakFactsHolder implements FactsHolder {
        @Override
        public int getPriorIndex(TimeAndDims key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getMinTimeMillis() {
            return 0;
        }

        @Override
        public long getMaxTimeMillis() {
            return 0;
        }

        @Override
        public Iterator<TimeAndDims> iterator(boolean descending) {
            return descending ? rows.keySet().descendingIterator() : rows.keySet().iterator();
        }

        @Override
        public Iterable<TimeAndDims> timeRangeIterable(boolean descending, long timeStart, long timeEnd) {
            return null;
        }

        @Override
        public Iterable<TimeAndDims> keySet() {
            return rows.keySet();
        }

        @Override
        public int putIfAbsent(TimeAndDims key, int rowIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {

        }
    }


    @Override
    protected Integer addToFacts(AggregatorFactory[] metrics, boolean deserializeComplexMetrics, boolean reportParseExceptions, InputRow row, AtomicInteger numEntries, TimeAndDims key, ThreadLocal<InputRow> rowContainer, Supplier<InputRow> rowSupplier) throws IndexSizeExceededException {
        if(metrics.length > 0 && getAggs()[0] == null) {
            // init aggreagators lazily
        }

        rows.compute(key, (k, v) -> v == null ? initBuffer(k, row) : aggregateValue(v, row) );
        return rows.size();
    }

    @Override
    public FactsHolder getFacts() {
        return factsHolder;
    }

    @Override
    public boolean canAppendRow() {
        return false;
    }

    @Override
    public String getOutOfRowsReason() {
        return null;
    }

    // copied from OffheapIncrementalIndex
    @Override
    protected BufferAggregator[] initAggs(AggregatorFactory[] metrics, Supplier<InputRow> rowSupplier, boolean deserializeComplexMetrics) {
        selectors = Maps.newHashMap();
        aggOffsetInBuffer = new int[metrics.length];

        for (int i = 0; i < metrics.length; i++) {
            AggregatorFactory agg = metrics[i];

            ColumnSelectorFactory columnSelectorFactory = makeColumnSelectorFactory(
                    agg,
                    rowSupplier,
                    deserializeComplexMetrics
            );

            selectors.put(
                    agg.getName(),
                    new OnheapIncrementalIndex.ObjectCachingColumnSelectorFactory(columnSelectorFactory)
            );

            if (i == 0) {
                aggOffsetInBuffer[i] = 0;
            } else {
                aggOffsetInBuffer[i] = aggOffsetInBuffer[i-1] + metrics[i-1].getMaxIntermediateSize();
            }
        }

        aggsTotalSize = aggOffsetInBuffer[metrics.length - 1] + metrics[metrics.length - 1].getMaxIntermediateSize();

        return new BufferAggregator[metrics.length];

    }


    @Override
    public int getLastRowIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BufferAggregator[] getAggsForRow(int rowOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object getAggVal(BufferAggregator agg, int rowOffset, int aggPosition) {
        return null;
    }

    @Override
    protected float getMetricFloatValue(int rowOffset, int aggOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected long getMetricLongValue(int rowOffset, int aggOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object getMetricObjectValue(int rowOffset, int aggOffset) {
        throw new UnsupportedOperationException();
    }


    // Override here to use oak entries iterator
    @Override
    public Iterable<Row> iterableWithPostAggregations(final List<PostAggregator> postAggs, final boolean descending) {
        return new Iterable<Row>()
        {
            @Override
            public Iterator<Row> iterator()
            {
                final List<DimensionDesc> dimensions = getDimensions();

                Iterator<Map.Entry<TimeAndDims, ByteBuffer>> entryIterator = descending ? rows.descendingMap().entrySet().iterator() : rows.entrySet().iterator();
                return Iterators.transform(
                        entryIterator,
                        entry  -> {
                            TimeAndDims timeAndDims = entry.getKey();


                            Object[] theDims = timeAndDims.getDims();

                            Map<String, Object> theVals = Maps.newLinkedHashMap();
                            for (int i = 0; i < theDims.length; ++i) {
                                Object dim = theDims[i];
                                DimensionDesc dimensionDesc = dimensions.get(i);
                                if (dimensionDesc == null) {
                                    continue;
                                }
                                String dimensionName = dimensionDesc.getName();
                                DimensionHandler handler = dimensionDesc.getHandler();
                                if (dim == null || handler.getLengthOfEncodedKeyComponent(dim) == 0) {
                                    theVals.put(dimensionName, null);
                                    continue;
                                }
                                final DimensionIndexer indexer = dimensionDesc.getIndexer();
                                Object rowVals = indexer.convertUnsortedEncodedKeyComponentToActualArrayOrList(dim, DimensionIndexer.LIST);
                                theVals.put(dimensionName, rowVals);
                            }

                            BufferAggregator[] aggs = getAggs();
                            for (int i = 0; i < aggs.length; ++i) {
                                theVals.put(getMetricNames().get(i), getAggVal(i, entry.getValue()));
                            }

                            if (postAggs != null) {
                                for (PostAggregator postAgg : postAggs) {
                                    theVals.put(postAgg.getName(), postAgg.compute(theVals));
                                }
                            }

                            return new MapBasedRow(timeAndDims.getTimestamp(), theVals);
                        }
                );
            }
        };

    }

    private Object getAggVal(int aggIdx, ByteBuffer value) {
        throw new UnsupportedOperationException();
    }
}
