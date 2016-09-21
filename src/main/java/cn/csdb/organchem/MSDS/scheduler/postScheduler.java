package cn.csdb.organchem.MSDS.scheduler;

import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

/**
 * Created by week on 2016/9/21.
 */
@ThreadSafe
public class postScheduler extends QueueScheduler {
    @Override
    public void push(Request request, Task task) {
        this.logger.trace("get a candidate url {}", request.getUrl());
        if(!/*this.duplicatedRemover.isDuplicate(request, task) ||*/ this.shouldReserved(request)) {
            this.logger.debug("push to queue method={},url={},Extras={}",request.getMethod(), request.getUrl(),request.getExtras());
            this.pushWhenNoDuplicate(request, task);
        }
    }
}
