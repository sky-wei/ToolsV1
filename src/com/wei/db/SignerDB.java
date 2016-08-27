package com.wei.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.wei.bean.SignerInfo;
import com.wei.tools.Tools;

public class SignerDB extends SqliteDB {

	public static final String DB_NAME = "signer.db";
	
	public SignerDB() {
		super(DB_NAME);
	}
	
	@Override
	public void create(Statement statement) {
		
		try {
			String sql = "create table signer("
					+ "id integer primary key autoincrement,"
					+ "keystorePassword varchar(100),"
					+ "term integer,"
					+ "signerName varchar(100),"
					+ "signerPassword varchar(100),"
					+ "name varchar(100),"
					+ "organization varchar(50),"
					+ "city varchar(50),"
					+ "province varchar(50),"
					+ "code varchar(20),"
					+ "filePath varchar(100),"
					+ "createTime varchar(50)"
					+ ");";
			
			statement.execute(sql);
		} catch (SQLException e) {
			Tools.log.error("执行SQL语句失败!", e);
		}
	}
	
	public List<SignerInfo> queryAllSignerInfo() {
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			String sql = "select id, keystorePassword, term, signerName, signerPassword, name, organization, city, province, code, filePath, createTime from signer;";
			
			conn = getConnection();
			preparedStatement = conn.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();
			
			List<SignerInfo> signerInfos = new ArrayList<SignerInfo>();
			
			while (resultSet.next()) {
				SignerInfo signerInfo = new SignerInfo();
				
				signerInfo.setId(resultSet.getInt("id"));
				signerInfo.setKeystorePassword(resultSet.getString("keystorePassword"));
				signerInfo.setTerm(resultSet.getInt("term"));
				signerInfo.setSignerName(resultSet.getString("signerName"));
				signerInfo.setSignerPassword(resultSet.getString("signerPassword"));
				signerInfo.setName(resultSet.getString("name"));
				signerInfo.setOrganization(resultSet.getString("organization"));
				signerInfo.setCity(resultSet.getString("city"));
				signerInfo.setProvince(resultSet.getString("province"));
				signerInfo.setCode(resultSet.getString("code"));
				signerInfo.setFilePath(new File(resultSet.getString("filePath")));
				signerInfo.setCreateTime(resultSet.getString("createTime"));
				
				signerInfos.add(signerInfo);
			}
			
			return signerInfos;
		} catch (ClassNotFoundException e) {
			Tools.log.error("ClassNotFoundException", e);
		} catch (SQLException e) {
			Tools.log.error("SQLException", e);
		} finally {
			closeResultSet(resultSet);
			closeStatement(preparedStatement);
			closeConnection(conn);
		}
		return null;
	}
	
	public SignerInfo querySignerInfo(int id) {
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			String sql = "select id, keystorePassword, term, signerName, signerPassword, name, organization, city, province, code, filePath, createTime from signer where id = ?;";
			
			conn = getConnection();
			preparedStatement = conn.prepareStatement(sql);
			
			preparedStatement.setInt(1, id);
			
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				SignerInfo signerInfo = new SignerInfo();
				
				signerInfo.setId(resultSet.getInt("id"));
				signerInfo.setKeystorePassword(resultSet.getString("keystorePassword"));
				signerInfo.setTerm(resultSet.getInt("term"));
				signerInfo.setSignerName(resultSet.getString("signerName"));
				signerInfo.setSignerPassword(resultSet.getString("signerPassword"));
				signerInfo.setName(resultSet.getString("name"));
				signerInfo.setOrganization(resultSet.getString("organization"));
				signerInfo.setCity(resultSet.getString("city"));
				signerInfo.setProvince(resultSet.getString("province"));
				signerInfo.setCode(resultSet.getString("code"));
				signerInfo.setFilePath(new File(resultSet.getString("filePath")));
				signerInfo.setCreateTime(resultSet.getString("createTime"));
				
				return signerInfo;
			}
		} catch (ClassNotFoundException e) {
			Tools.log.error("ClassNotFoundException", e);
		} catch (SQLException e) {
			Tools.log.error("SQLException", e);
		} finally {
			closeResultSet(resultSet);
			closeStatement(preparedStatement);
			closeConnection(conn);
		}
		return null;
	}
	
	public boolean insertSignerInfo(SignerInfo signerInfo) {
		
		if (signerInfo == null)	return false;
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			String sql = "insert into signer(keystorePassword, term, signerName, signerPassword, name, organization, city, province, code, filePath, createTime)"
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			
			conn = getConnection();
			preparedStatement = conn.prepareStatement(sql);
			
			preparedStatement.setString(1, signerInfo.getKeystorePassword());
			preparedStatement.setInt(2, signerInfo.getTerm());
			preparedStatement.setString(3, signerInfo.getSignerName());
			preparedStatement.setString(4, signerInfo.getSignerPassword());
			preparedStatement.setString(5, signerInfo.getName());
			preparedStatement.setString(6, signerInfo.getOrganization());
			preparedStatement.setString(7, signerInfo.getCity());
			preparedStatement.setString(8, signerInfo.getProvince());
			preparedStatement.setString(9, signerInfo.getCode());
			preparedStatement.setString(10, signerInfo.getFilePath().getPath());
			preparedStatement.setString(11, signerInfo.getCreateTime());
			
			int reuslt = preparedStatement.executeUpdate();
			return reuslt > 0 ? true : false;
		} catch (ClassNotFoundException e) {
			Tools.log.error("ClassNotFoundException", e);
		} catch (SQLException e) {
			Tools.log.error("SQLException", e);
		} finally {
			closeResultSet(resultSet);
			closeStatement(preparedStatement);
			closeConnection(conn);
		}
		return false;
	}
	
	public boolean deleteSignerInfo(int id) {
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			String sql = "delete from signer where id = ?;";
			
			conn = getConnection();
			preparedStatement = conn.prepareStatement(sql);
			
			preparedStatement.setInt(1, id);
			
			int reuslt = preparedStatement.executeUpdate();
			return reuslt > 0 ? true : false;
		} catch (ClassNotFoundException e) {
			Tools.log.error("ClassNotFoundException", e);
		} catch (SQLException e) {
			Tools.log.error("SQLException", e);
		} finally {
			closeResultSet(resultSet);
			closeStatement(preparedStatement);
			closeConnection(conn);
		}
		return false;
	}
	
	public boolean updateSignerInfo(SignerInfo signerInfo) {
		
		if (signerInfo == null)	return false;
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			String sql = "update signer set keystorePassword = ?, term = ?, signerName = ?, signerPassword = ?, name = ?, organization = ?,"
					+ "city = ?, province = ?, code = ?, filePath = ?, createTime = ? where id = ?;";
			
			conn = getConnection();
			preparedStatement = conn.prepareStatement(sql);
			
			preparedStatement.setString(1, signerInfo.getKeystorePassword());
			preparedStatement.setInt(2, signerInfo.getTerm());
			preparedStatement.setString(3, signerInfo.getSignerName());
			preparedStatement.setString(4, signerInfo.getSignerPassword());
			preparedStatement.setString(5, signerInfo.getName());
			preparedStatement.setString(6, signerInfo.getOrganization());
			preparedStatement.setString(7, signerInfo.getCity());
			preparedStatement.setString(8, signerInfo.getProvince());
			preparedStatement.setString(9, signerInfo.getCode());
			preparedStatement.setString(10, signerInfo.getFilePath().getPath());
			preparedStatement.setString(11, signerInfo.getCreateTime());
			preparedStatement.setInt(12, signerInfo.getId());
			
			int reuslt = preparedStatement.executeUpdate();
			return reuslt > 0 ? true : false;
		} catch (ClassNotFoundException e) {
			Tools.log.error("ClassNotFoundException", e);
		} catch (SQLException e) {
			Tools.log.error("SQLException", e);
		} finally {
			closeResultSet(resultSet);
			closeStatement(preparedStatement);
			closeConnection(conn);
		}
		return false;
	}
}
