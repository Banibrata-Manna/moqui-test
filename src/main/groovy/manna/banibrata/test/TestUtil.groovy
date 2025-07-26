package manna.banibrata.test

import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.Timestamp
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

public class TestUtil {
  protected final static Logger logger = LoggerFactory.getLogger(TestUtil.class);

  public static Long bulkCreateTest(ExecutionContext ec) {
    // TODO: Use this also, need to check performance with this disabled.
    boolean useBatch = ec.context.get("useBatch");
    long batchSize = (Long) ec.context.get("batchSize");
    long max = 1000000;

    AtomicLong recordNumber = new AtomicLong(0);
    AtomicInteger loopCounter = new AtomicInteger(1);

    logger.info("Start Time  : " + ec.user.nowTimestamp);

    while (recordNumber.get() < max) {
      Thread thread = new Thread(() -> {
        List<EntityValue> entityValues = new ArrayList<>();
        for (long i = 0; i < batchSize; i++) {
          long currentRecord = recordNumber.getAndIncrement();
          if (currentRecord >= max) break;

          EntityValue ev = ec.entity.makeValue("manna.banibrata.test.Test").setAll([
              testId: "T-" + loopCounter.get() + " CR-" + currentRecord,
              testDescription: "Description of Test : " + currentRecord
          ]);
          entityValues.add(ev);
        }

        if (!entityValues.isEmpty()) {
          ec.entity.createBulk(entityValues);
        }
      }, "T-" + loopCounter.getAndIncrement());

      thread.start();
    }

    logger.info("End Time  : " + ec.user.nowTimestamp);
    return recordNumber.get();
  }

}