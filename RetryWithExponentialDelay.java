import android.annotation.SuppressLint;
import android.util.Log;
import androidx.annotation.NonNull;
import org.reactivestreams.Publisher;
import java.util.concurrent.TimeUnit;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RetryWithExponentialDelay implements Function<Flowable<? extends Throwable>, Publisher<?>> {
    private final String TAG = "jy_test";
    private int maxRetry;
    private int retryCount;

    public RetryWithExponentialDelay(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Publisher<?> apply(Flowable<? extends Throwable> throwable) {
        return throwable.flatMap(
                new Function<Throwable, Publisher<?>>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public Publisher<?> apply(@NonNull Throwable throwable) {
                        retryCount += 1;
                        if (retryCount < maxRetry) {
                            long retryDelaySec = (long) Math.pow(2, retryCount - 1);
                            Log.i(TAG, String.format("retry (%d/%d) '%s's 이후 재시도.. err_msg=%s", retryCount, maxRetry, retryDelaySec, throwable.getMessage()));
                            return Flowable.timer(retryDelaySec, TimeUnit.SECONDS);
                        }
                        return Flowable.error(throwable);
                    }
                });
    }
}
