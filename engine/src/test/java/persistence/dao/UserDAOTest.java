package persistence.dao;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.UserObject;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.dao.UserDAO;
import br.com.engine.persistence.dao.UserGroupDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/applicationContext.xml" })
@Transactional
public class UserDAOTest {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserGroupDAO userGroupDAO;

	@Test
	@Ignore
	public void listAllUsers() throws Exception {
		List<User> listUser = this.userDAO.list();
		assert (listUser.size() > 0);
	}
	
	@Test
	@Ignore
	public void deleteUser() throws Exception {
		User user = this.userDAO.find("loginQualquer");
		this.userDAO.destroy(user);
		
		User user2 = this.userDAO.find("loginQualquer");
		assert (user2 == null);
	}
	
	@Test
	@Ignore
	public void createUser() throws Exception {
		UserGroup userGroup = userGroupDAO.find("nameGroup");
		UserObject userObject = new UserObject();
		userObject.setLogin("loginQualquer");
		userObject.setPassword("senhaQuallquer");
		this.userDAO.create(userObject, userGroup);
	}
}
