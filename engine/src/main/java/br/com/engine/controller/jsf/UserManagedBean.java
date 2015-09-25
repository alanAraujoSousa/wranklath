package br.com.engine.controller.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.springframework.stereotype.Component;

import br.com.engine.business.service.UserService;

@Component
@ManagedBean
@SessionScoped
public class UserManagedBean {

	@ManagedProperty("#{userService}")
	private UserService userService;

	private String userName;
	public String password;
	public String msg;

	public String login() {
		try {
			userService.login(getUserName(), password);
			return "jsf/page/main";
		} catch (Exception e) {
			msg = e.getMessage();
			return null;
		}
	}

	public String logout() {
		String token = userService.login(getUserName(), password);
		try {
			userService.logout(token);
		} catch (Exception e) {
			msg = e.getMessage();
		}
		return "index";
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Changes the value of password as the parameter.
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the msg.
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Changes the value of msg as the parameter.
	 *
	 * @param msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Changes the value of userName as the parameter.
	 *
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the userController.
	 */
	public UserService getUserController() {
		return userService;
	}

	/**
	 * Changes the value of userController as the parameter.
	 *
	 * @param userController
	 */
	public void setUserController(UserService userController) {
		this.userService = userController;
	}
}
