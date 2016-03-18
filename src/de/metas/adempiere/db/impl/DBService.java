package de.metas.adempiere.db.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import de.metas.adempiere.db.IDBService;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Trx;

public final class DBService implements IDBService {

	private static final CLogger logger = CLogger.getCLogger(DBService.class);

	final Set<String> trxNames = new HashSet<String>();

	/**
	 * Invokes {@link org.compiere.util.DB#prepareStatement(String, String)}.
	 */
	public PreparedStatement mkPstmt(final String sql, final String trxName) {

		checkTrxName(trxName);
		return DB.prepareStatement(sql, trxName);
	}

	private void checkTrxName(final String trxName) {

		if (!trxNames.contains(trxName)) {

			final String msg = "A Trx with name '"
					+ trxName
					+ "' has not been created using createTrx() or has already been closed";
			logger.fine(msg);

			// throw new IllegalStateException(msg);
		}
	}

	/**
	 * Invokes {@link org.compiere.util.DB#close(ResultSet, java.sql.Statement)}.
	 */
	public void close(ResultSet rs, PreparedStatement pstmt) {
		DB.close(rs, pstmt);
	}

	public boolean closeTrx(String trxName) {

		trxNames.remove(trxName);
		return getExisting(trxName).close();
	}

	public boolean commitTrx(String trxName) {

		return getExisting(trxName).commit();
	}

	public String createTrx(String prefix) {

		final String trxName = Trx.createTrxName(prefix);
		Trx.get(trxName, true);
		return trxName;
	}

	private Trx getExisting(final String trxName) {

		checkTrxName(trxName);

		final Trx trx = Trx.get(trxName, false);
		return trx;
	}

	public boolean rollBackTrx(String trxName) {

		return getExisting(trxName).rollback();
	}

	public void close(PreparedStatement pstmt) {
		DB.close(pstmt);
	}

	/**
	 * Invokes {@link DB#getSQLValueEx(String, String, Object...)}.
	 * 
	 * @param trxName
	 * @param sql
	 * @param params
	 * @return
	 */
	public int getSQLValueEx(final String trxName, final String sql,
			final Object... params) {

		return DB.getSQLValueEx(trxName, sql, params);
	}

}
