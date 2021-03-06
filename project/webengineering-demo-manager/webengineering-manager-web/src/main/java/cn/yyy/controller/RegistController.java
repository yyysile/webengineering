package cn.yyy.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.yyy.pojo.ImgCheckCode;
import cn.yyy.pojo.Message;
import cn.yyy.pojo.MessageInfo;
import cn.yyy.pojo.PageBean;
import cn.yyy.pojo.StudentInfo;
import cn.yyy.pojo.TeacherInfo;
import cn.yyy.pojo.User;
import cn.yyy.service.MessageInfoService;
import cn.yyy.service.MessageService;
import cn.yyy.service.RegisterService;
import cn.yyy.service.StudentService;
import cn.yyy.service.TeacherService;
import cn.yyy.service.UserService;
import common.utils.JsonUtil;
import common.utils.SendPhoneMsg;

@Controller
public class RegistController {
	@Autowired
	private RegisterService registerService;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	@Autowired
	private MessageInfoService messageInfoService;
	@Autowired
	private StudentService studentService;

	@RequestMapping("/getcheckcode")
	@ResponseBody
	public void getcheckcode(HttpSession session,HttpServletResponse response) {
		ImgCheckCode imgCheckCode=new ImgCheckCode();
		final int width=80;
		final int height=35;
		imgCheckCode=registerService.getImgCheckCode(width, height);
		System.out.println(imgCheckCode.getCheckCode());
		session.setAttribute("checkcodestr",imgCheckCode.getCheckCode());
		try {
			ImageIO.write( imgCheckCode.getCheckCodeImg(), "jpeg", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	@RequestMapping("/sendPhoneCheckCode")
	@ResponseBody
	public String sendPhoneCheckCode(String phone,HttpSession session) {
		String code = "123456";
		boolean isSuccess = false;
		System.out.println(phone);
		if (code != null) {
			session.setAttribute("phoneCode", code);
			isSuccess = true;
		}
		String result = "{\"isSuccess\":"+isSuccess+"}";
		return result;
	}
	
	@RequestMapping("/checkusername")
	@ResponseBody
	public String checkUserName(String username,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> result=new HashMap<String, Object>();
		System.out.println(username);
		boolean sign=registerService.isExistUsername(username);
		
		result.put("isnameexist", sign);
		session.setAttribute("isusernameexist", sign);
		String json = null;
		try {
			json = JsonUtil.mapToJson(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println(json);
		return json;
	}
	
	
	@RequestMapping("/checkphonenumber")
	@ResponseBody
	public String checkPhoneNumber(String phonenumber,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> result=new HashMap<String, Object>();
		System.out.println(phonenumber);
		boolean sign=registerService.isExistPhoneNumber(phonenumber);
		result.put("isphoneexist", sign);
		String json = null;
		try {
			json = JsonUtil.mapToJson(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		//System.out.println(json);
		return json;
	}
	
	@RequestMapping("/checkccode")
	@ResponseBody
	public String checkCCode(String checkcode,HttpSession session) {
		Map<String, Object> result=new HashMap<String, Object>();
		System.out.println("client:"+checkcode);
		String sessioncheckcode=(String) session.getAttribute("checkcodestr");
		boolean sign=false;
		if(sessioncheckcode.equals(checkcode)) {
			sign=true;
		}
		
		result.put("ischeckcoderight", sign);
		String json = null;
		try {
			json = JsonUtil.mapToJson(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println(json);
		return json;
	}
	
	@RequestMapping("/checkPhoneCode")
	@ResponseBody
	public String checkPhoneCode(String phoneCode,HttpSession session) {
		Object codeobj = session.getAttribute("phoneCode");
		String code = null;
		boolean isRight = false;
		if (codeobj != null) {
			code = (String) codeobj;
			if (phoneCode.equals(code)) {
				isRight = true;
				System.out.println(code);
				session.removeAttribute("phoneCode");
			}
		}
		String result = "{\"isRight\":"+isRight+"}";
		return result;
	}
	
	@RequestMapping("/addNewTeacher")
	@ResponseBody
	public String addNewTeacher(String username,String password,String phone,String name,String staffid,String mail,String schoolName,String collegeName,HttpSession session) {
		TeacherInfo teacherInfo = new TeacherInfo();
		teacherInfo.setCollegeName(collegeName);
		teacherInfo.setHeadicon("");
		teacherInfo.setIdno("");
		teacherInfo.setMail(mail);
		teacherInfo.setName(name);
		teacherInfo.setPassword(password);
		teacherInfo.setPhone(phone);
		teacherInfo.setSchoolName("\""+schoolName+"\"");
		teacherInfo.setStaffid(Integer.parseInt(staffid));
		teacherInfo.setTeacherid(null);
		teacherInfo.setUserid(null);
		teacherInfo.setUsername(username);
		System.out.println("collegeName:"+ collegeName);
		System.out.println("mail:"+ mail);
		System.out.println("name:"+ name);
		System.out.println("password:"+ password);
		System.out.println("phone:"+ phone);
		System.out.println("schoolName:"+ schoolName);
		System.out.println("staffid:"+ staffid);
		System.out.println("username:"+ username);
		teacherService.addNewTeacher(teacherInfo);
		User user = userService.getUserByUsername(username);
		teacherInfo = teacherService.getTeacherInfoByUid(user.getUserid());
		session.setAttribute("user", teacherInfo);
		session.setAttribute("identity", "teacher");
		return "";
	}
	
	@RequestMapping("/addNewStudent")
	@ResponseBody
	public String addNewStudent(String username,String password,String phone,String name,String staffid,String mail,String schoolName,String collegeName,HttpSession session) {
		StudentInfo studentInfo = new StudentInfo();
		studentInfo.setCollegename(collegeName);
		studentInfo.setHeadicon("");
		studentInfo.setIdno("");
		studentInfo.setMail(mail);
		studentInfo.setName(name);
		studentInfo.setPassword(password);
		studentInfo.setPhone(phone);
		studentInfo.setSchoolname("\""+schoolName+"\"");
		studentInfo.setSid(Integer.parseInt(staffid));
		studentInfo.setStudentid(null);
		studentInfo.setUserid(null);
		studentInfo.setUsername(username);
		System.out.println("collegeName:"+ collegeName);
		System.out.println("mail:"+ mail);
		System.out.println("name:"+ name);
		System.out.println("password:"+ password);
		System.out.println("phone:"+ phone);
		System.out.println("schoolName:"+ schoolName);
		System.out.println("staffid:"+ staffid);
		System.out.println("username:"+ username);
		studentService.addNewStudent(studentInfo);
		User user = userService.getUserByUsername(username);
		studentInfo = studentService.getStudentInfoByUid(user.getUserid());
		session.setAttribute("user", studentInfo);
		session.setAttribute("identity", "student");
		return "";
	}
	
	@RequestMapping("/jumpToUserIndex")
	public String jumpToUserIndex(HttpSession session) {
		System.out.println("跳转至主页");
		Object user = session.getAttribute("user");
		if (user == null)
			return "index";
		Object identity = session.getAttribute("identity");
		if (identity == null)
			return "index";
		else {
			String identitystr = (String)identity;
			if (identitystr.equals("teacher")) {
				getMessageInfosByUid(((TeacherInfo)user).getUserid(),0,3,session);
				return "teacherindex";
			}else if (identitystr.equals("student")) {
				getMessageInfosByUid(((StudentInfo)user).getUserid(),0,3,session);
				return "studentindex";
			}else {
				return "index";
			}
		}
		
		
	}
	
	@RequestMapping("/userIndex")
	public String userIndex(HttpSession session) {
		//System.out.println("跳转至主页");
		Object user = session.getAttribute("user");
		if (user == null)
			return "index";
		Object identity = session.getAttribute("identity");
		if (identity == null)
			return "index";
		else {
			String identitystr = (String)identity;
			if (identitystr.equals("teacher")) {
				return "teacherindex";
			}else if (identitystr.equals("student")) {
				return "studentindex";
			}else {
				return "index";
			}
		}
		
		
	}
	
	@RequestMapping("/updateUserMessage")
	@ResponseBody
	public void updateUserMessage(int pageIndex,HttpSession session) {
		System.out.println("更新消息页");
		System.out.println("pageIndex:"+pageIndex);
		Object user = session.getAttribute("user");
		if (user == null)
			return;
		Object identity = session.getAttribute("identity");
		if (identity == null)
			return;
		else {
			String identitystr = (String)identity;
			if (identitystr.equals("teacher")) {
				getMessageInfosByUid(((TeacherInfo)user).getUserid(),pageIndex,3,session);
				return;
			}else if (identitystr.equals("student")) {
				getMessageInfosByUid(((StudentInfo)user).getUserid(),pageIndex,3,session);
				System.out.println("更新学生成功");
				return;
			}else {
				return;
			}
		}
		
		
	}
	
	private void getMessageInfosByUid(Integer userid,int pageIndex,int pageSize,HttpSession session) {
		System.out.println("进来的index"+pageIndex);
		PageBean<Message> messagesbean = messageService.getMessageByReceiveId(userid, pageIndex, pageSize);
		session.setAttribute("messagebean", messagesbean);
		List<MessageInfo> messages = new ArrayList<>();
		for (Message message : messagesbean.getPageDatas()) {
			System.out.println(message.getTitle());
			MessageInfo messageInfo = messageInfoService.getMessageInfoByMessage(message);
			System.out.println(messageInfo.getShortMsg());
			if (!messages.contains(messageInfo))
				messages.add(messageInfo);
		}
		session.setAttribute("messages", messages);
		
	}
}
