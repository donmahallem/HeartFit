package com.github.donmahallem.heartfit.pagination;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import timber.log.Timber;

public class UserWeightDataSource
        extends ItemKeyedDataSource<Long, WeightTimestamp> {

    private final HistoryClient mHistoryClient;

    public UserWeightDataSource(HistoryClient historyClient) {
        this.mHistoryClient = historyClient;
    }

    @NonNull
    @Override
    public Long getKey(@NonNull WeightTimestamp item) {
        return item.getTimestamp();
    }

    public List<WeightTimestamp> convertDatasetsToList(List<DataSet> dataSets) {
        List<WeightTimestamp> list = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            Timber.d("Dataset points: %s",dataSet.getDataPoints().size());
            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                list.add(new WeightTimestamp(dataPoint.getTimestamp(TimeUnit.MILLISECONDS), dataPoint.getValue(Field.FIELD_WEIGHT).asFloat()));
            }

        }
        return list;
    }

    public List<WeightTimestamp> convertBucketsToList(List<Bucket> buckets) {
        List<WeightTimestamp> list = new ArrayList<>();
        for (Bucket bucket : buckets) {
            for (DataSet dataSet : bucket.getDataSets()) {
                for (DataPoint dataPoint : dataSet.getDataPoints()) {
                    list.add(new WeightTimestamp(dataPoint.getTimestamp(TimeUnit.MILLISECONDS), dataPoint.getValue(Field.FIELD_WEIGHT).asFloat()));
                }
            }
        }
        return list;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<WeightTimestamp> callback) {
        final long endTime = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1).toEpochSecond();
        final long startTime = endTime - (31 * 24 * 60 * 60 * 365);
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
                .read(DataType.TYPE_WEIGHT)
                .setLimit(params.requestedLoadSize)
                .enableServerQueries()
                .build();
        DataReadResponse response = null;
        try {
            response = Tasks.await(this.mHistoryClient.readData(dataReadRequest), 10, TimeUnit.SECONDS);
            if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
                List<WeightTimestamp> listData = convertDatasetsToList(response.getDataSets());
                Collections.sort(listData, new Comparator<WeightTimestamp>() {
                    @Override
                    public int compare(WeightTimestamp weightTimestamp, WeightTimestamp t1) {
                        return (int) (t1.getTimestamp() - weightTimestamp.getTimestamp());
                    }
                });
                Timber.d("data loaded. Items: %s %s", listData.size(), response.getDataSets().size());
                callback.onResult(listData);
            } else {
                Timber.e(response.getStatus().toString());
                callback.onResult(Collections.<WeightTimestamp>emptyList());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAfter(@NonNull ItemKeyedDataSource.LoadParams<Long> params,
                          @NonNull LoadCallback<WeightTimestamp> callback) {
        final long startTime = params.key - (31 * 24 * 60 * 60 * 365);
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, params.key - 1, TimeUnit.SECONDS)
                .read(DataType.TYPE_WEIGHT)
                .setLimit(params.requestedLoadSize)
                .enableServerQueries()
                .build();
        Timber.d("loadAfter %s - %s", startTime, params.key);
        DataReadResponse response = null;
        try {
            Task<DataReadResponse> ttask = this.mHistoryClient.readData(dataReadRequest);
            response = Tasks.await(ttask, 10, TimeUnit.SECONDS);
            if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
                List<WeightTimestamp> listData = convertDatasetsToList(response.getDataSets());
                Timber.d("data loaded. Items: %s %s", listData.size(), response.getDataSets().size());
                Collections.sort(listData, new Comparator<WeightTimestamp>() {
                    @Override
                    public int compare(WeightTimestamp weightTimestamp, WeightTimestamp t1) {
                        return (int) (t1.getTimestamp() - weightTimestamp.getTimestamp());
                    }
                });
                callback.onResult(listData);
            } else {
                Timber.e(ttask.getException());
                callback.onResult(Collections.<WeightTimestamp>emptyList());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<WeightTimestamp> callback) {

    }
}