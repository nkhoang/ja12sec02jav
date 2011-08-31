package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.CurrencyDao;
import com.nkhoang.gae.dao.MessageDao;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.model.GoldPrice;
import com.nkhoang.gae.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Currency dao implementation.
 *
 * @author hnguyen93
 */
@Transactional
public class MessageDaoImpl extends GeneralDaoImpl<Message, Long> implements MessageDao {
	private static final Logger LOGGER                          = LoggerFactory.getLogger(MessageDaoImpl.class);
	private static final int    MAXIMUM_MESSAGE_IN_QUEUE        = 300;
	private static final int    MAXIMUM_MESSAGE_RETAIN_IN_QUEUE = 100;

	@Override
	public Message save(Message e) {
		return super.save(e);
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(
			entityManager);    //To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * Get a message from DB.
	 *
	 * @param id: message id.
	 *
	 * @return an object
	 *         or
	 *         null value.
	 */
	public Message get(Long id) {
		LOGGER.info(String.format("Get message with [id: %s].", id));
		try {
			Query query = entityManager
				.createQuery(String.format("Select from %s t where t.id=:messageID", Message.class.getName()));
			query.setParameter("messageID", id);

			Message m = (Message) query.getSingleResult();
			if (m != null) {
				return m;
			}
		}
		catch (Exception e) {
			LOGGER.info("Failed to get Exchange rate from DB.");
		}
		return null;
	}

	/**
	 * Get all messages from DB.
	 *
	 * @return a list
	 *         or
	 *         null value.
	 */
	public List<Message> getAll() {
		LOGGER.info("Get all messages ...");
		List<Message> result = null;
		try {
			Query query = entityManager.createQuery(String.format("Select from %s", Message.class.getName()));

			result = query.getResultList();
		}
		catch (Exception ex) {
			LOGGER.error("Failed to load all messages from DB.", ex);
		}
		return result;
	}


	/**
	 * Delete an exchange rate from DB.
	 *
	 * @param id: exchange rate id.
	 */
	public boolean delete(Long id) {
		LOGGER.info("Delete exchange rate with [id: " + id + "].");
		boolean result = false;
		try {
			Query query = entityManager.createQuery("Delete from " + Currency.class.getName() + " i where i.id=" + id);
			query.executeUpdate();
			entityManager.flush();

			result = true;
		}
		catch (Exception e) {
			LOGGER.error("Failed to exchange rate with [id:" + id + "]", e);
		}
		return result;
	}

	@Override
	public Message getLatestMessage(int categoryId) {
		try {
			Query query = entityManager.createQuery(
				String.format(
					"Select from %s t where t.categoryId=:category order by t.time DESC", Message.class.getName()));
			query.setParameter("category", categoryId);

			Message m = (Message) query.getSingleResult();

			return m;
		}
		catch (Exception e) {
			LOGGER.error("Failed to get the latest messasge", e);
		}
		return null;
	}

	@Override
	public List<Message> getLatestMessages(int categoryId, int interval) {
		List<Message> messages = new ArrayList<Message>();
		try {
			Query query = entityManager.createQuery(
				String.format(
					"Select from %s t where t.categoryId=:category and t.time >= :fromTime and t.time <= :toTime order by t.time DESC",
					Message.class.getName()));
			query.setParameter("category", categoryId);
			query.setParameter("fromTime", System.currentTimeMillis() - (interval * 1000));
			query.setParameter("toTime", System.currentTimeMillis());

			messages = (List<Message>) query.getResultList();
		}
		catch (Exception e) {
			LOGGER.error("Failed to get the latest messasges", e);
		}
		return messages;
	}

	@Override
	public void cleanupMessageQueue() {
		List<Message> messages = new ArrayList<Message>();
		Query query = entityManager.createQuery(
			String.format(
				"Select from %s", Message.class.getName()));

		messages = (List<Message>) query.getResultList();
		if (messages.size() > MAXIMUM_MESSAGE_IN_QUEUE) {
			// perform delete.
			messages = messages.subList(MAXIMUM_MESSAGE_RETAIN_IN_QUEUE, messages.size());
			LOGGER.info(String.format("Numbers of message to be deleted: %s", messages.size()));
			for (Message message : messages) {
				delete(message.getId());
			}
		}
	}
}
