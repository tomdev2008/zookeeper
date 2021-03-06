package koolearn.framework.zookeeperassist.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/test")
public class MyController
{
	private String dataSourceTemplate = "";
	private ArrayList<String> userList = null;
	@Autowired
	private ZookeeperManagement zookeeperManagement = null;

	@RequestMapping("/index")
	public String index(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		return "monitor";
	}
	@RequestMapping("/login")
	public void login(@RequestParam("username") String userName, @RequestParam("password") String password, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		PrintWriter printWriter = httpServletResponse.getWriter();
		for (String user:this.userList) {
			String[] userFields = user.split(",");
			if (userFields.length < 3) {
				continue;
			}

			if (userName.equals(userFields[0]) && password.equals(userFields[1])) {
				String result = "[{\"path\":\"" + userFields[2] + "\"}";
				for (int index=3;index<userFields.length;++index) {
					result += ",{\"path\":\"" + userFields[index] + "\"}";
				}
				result += "]";
				printWriter.print(JSON.parse(result));
				break;
			}
		}
		printWriter.close();
	}
	@RequestMapping("/connectzookeeper")
	public void connectZookeeper(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		String zookeeperAddress = httpServletRequest.getParameter("zookeeperAddress");
		PrintWriter printWriter = httpServletResponse.getWriter();
		if (this.zookeeperManagement.open(zookeeperAddress))
		{
			printWriter.print("true");
		}
		else
		{
			printWriter.print("false");
		}
		printWriter.close();
	}
	@RequestMapping("/closezookeeper")
	public void closeZookeeper(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		this.zookeeperManagement.close();
	}
	@RequestMapping("/updatedata")
	public void updateData(@RequestParam("nodeName") String nodeName, @RequestParam("nodeData") String nodeData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		String formatNodeName = nodeName.substring(nodeName.indexOf("/"));
		this.zookeeperManagement.setData(formatNodeName, nodeData);
		PrintWriter printWriter = httpServletResponse.getWriter();
		printWriter.print("ok");
		printWriter.close();
	}
	@RequestMapping("/adddata")
	public void addData(@RequestParam("nodeName") String nodeName, @RequestParam("newNodeName") String newNodeName, @RequestParam("newNodeData") String newNodeData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		String formatNodeName = nodeName.substring(nodeName.indexOf("/")) + "/" + newNodeName;
		this.zookeeperManagement.createData(formatNodeName, newNodeData);
		PrintWriter printWriter = httpServletResponse.getWriter();
		printWriter.print("ok");
		printWriter.close();
	}
	@RequestMapping("/adddatasource")
	public void addDataSource(@RequestParam("rootNodeName") String rootNodeName, @RequestParam("dataSourceName") String dataSourceName, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		this.zookeeperManagement.addDataSource(rootNodeName + "/" + this.dataSourceTemplate, dataSourceName, true);
		PrintWriter printWriter = httpServletResponse.getWriter();
		printWriter.print("ok");
		printWriter.close();
	}
	@RequestMapping("/nodeselect")
	public void nodeSelect(@RequestParam("nodeName") String nodeName, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		PrintWriter printWriter = httpServletResponse.getWriter();
		String value = this.zookeeperManagement.getData(nodeName);
		List<String> nodeList = this.zookeeperManagement.getChildren(nodeName);
		String result = "[{\"node\":\"" + nodeName + "\",\"value\":\"" + value + "\"},";
		for (String item : nodeList)
		{
			try
			{
				value = this.zookeeperManagement.getData(nodeName + "/" + item);
				result += "{\"node\":\"" + nodeName + "/" + item + "\",\"value\":\"" + value + "\"}" + ",";
			}
			catch (Exception e)
			{
			}
		}
		result = result.substring(0, result.length() - 1) + "]";
		printWriter.print(JSON.parse(result));
		printWriter.close();
	}
	@RequestMapping("/loadtree")
	public void loadTree(@RequestParam("category") String category, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		UUID rootId = UUID.randomUUID();
		this.zookeeperManagement.setJson("");
		this.zookeeperManagement.getJson(category, rootId, rootId, true);
		PrintWriter printWriter = httpServletResponse.getWriter();
		String result = this.zookeeperManagement.getJson();
		result = "[" + result.substring(0, result.length() - 1) + "]";
		printWriter.print(JSON.parse(result));
		printWriter.close();
	}
	public void setDataSourceTemplate(String dataSourceTemplate) {
		this.dataSourceTemplate = dataSourceTemplate;
	}
	public void setUserList(ArrayList<String> userList) {
		this.userList = userList;
	}
	public void setZookeeperManagement(ZookeeperManagement zookeeperManagement) {
		this.zookeeperManagement = zookeeperManagement;
	}
	
	public static void main(String[] args){
		 System.out.println("1");
	}
}