package fr.meuret.webtesttech.nio;

import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;


/**
 * Created by meuj on 10/6/2014.
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, Session> {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WriteCompletionHandler.class);

    @Override
    public void completed(Integer bytesWritten, Session session) {


        logger.debug("CompletionHandler : WRITE {} bytes : {}", bytesWritten, session.getRemoteAddress());
        ByteBuffer next;
        Queue<ByteBuffer> writeQueue = session.getWriteQueue();
        synchronized (writeQueue) {
            next = writeQueue.peek();
            if (!next.hasRemaining()) {
                writeQueue.remove();
                next = writeQueue.peek();
            }
        }
        if (next != null) {
            logger.debug("{} Pending write from completionHandler : {} ", next);
            session.pendingWrite(next);
        }


    }

    @Override
    public void failed(Throwable exc, Session session) {
        logger.debug("CompletionHandler : FAILED ", exc);
    }
}