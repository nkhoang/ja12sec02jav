package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Message;

import java.util.List;

public interface MessageDao extends BaseDao<Message, Long> {
	public Message getLatestMessage(int categoryId);

	/**
	 * Get latest messsage from the queue base on the time interval.
	 *
	 * @param categoryId category id (constant from Message class).
	 * @param interval   time interval in second.
	 *
	 * @return the latest messages.
	 */
	public List<Message> getLatestMessages(int categoryId, int interval);

	public void cleanupMessageQueue();
}
