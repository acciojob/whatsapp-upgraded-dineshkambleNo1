package com.driver;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WhatsappService {


    Map<String, User> userMap = new HashMap<>();
    Map<Group,List<User>> groupMap=new HashMap<>();

    List<Message> messageList=new ArrayList<>();

    Map<Group,List<Message>> messagesInGroup=new HashMap<>();

    Map<User,List<Message>> userMessageList=new HashMap<>();

    public String createUser(String name, String mobile) throws Exception {

        if (userMap.containsKey(mobile)) {
            throw new Exception("User already exists");
        }

        User user = new User(name, mobile);
        userMap.put(mobile, user);

        return "SUCCESS";
    }

    private int groupCount = 0;
    public Group createGroup(List<User> userList) {
        if(userList.size() == 2) {
            Group group = new Group(userList.get(1).getName(),2);
            groupMap.put(group,userList);
            return group;
        }


        Group group=new Group("Group "+ (groupCount + 1),userList.size());
        groupMap.put(group,userList);
        return group;
 }

    private int messageCount = 0 ;
    public int createMessage(String content)
    {

        Message message=new Message((++messageCount),content);
        message.setTimestamp(new Date());
        messageList.add(message);
        return messageCount;
    }



    public int sendMessage(Message message, User sender, Group group) throws Exception {

        if(!groupMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        boolean isSenderExists = false;

        for(User user : groupMap.get(group)){
            if(user.equals(sender)){
                isSenderExists = true;
                break;
            }
        }

        if(!isSenderExists) {
            throw new Exception("You are not allowed to send message");
        }

        if(messagesInGroup.containsKey(group)){
            messagesInGroup.get(group).add(message);
        }
        else {
            List<Message> msgs = new ArrayList<>();
            msgs.add(message);
            messagesInGroup.put(group,msgs);

        }

        if(userMessageList.containsKey(sender)){
            userMessageList.get(sender).add(message);
        }
        else{
            List<Message> msgs = new ArrayList<>();
            msgs.add(message);
            userMessageList.put(sender,msgs);
        }

        return messagesInGroup.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {

        if(!groupMap.containsKey(group))
        {
            throw new Exception("Group does not exists");
        }

        User pastAdmin = groupMap.get(group).get(0);

        if(!approver.equals(pastAdmin))
        {
            throw new Exception("Approver does not have rights");
        }

        boolean check = false;
        for(User user1:groupMap.get(group))
        {
            if(user1.equals(user))  check = true;
        }

        if(!check){
            throw new Exception("User is not a participant");
        }

        User newAdmin=null;

        Iterator<User> userIterator = groupMap.get(group).iterator();

        while(userIterator.hasNext())
        {
            User u= userIterator.next();
            if(u.equals(user))
            {
                newAdmin = u;
                userIterator.remove();
            }
        }

        groupMap.get(group).add(0,newAdmin);


        return  "SUCCESS";

    }


    public int removeUser(User user) throws Exception {

        boolean userFound = false;
        int groupSize = 0;
        int messageCnt = 0;
        int overallMsgCnt = messageList.size();

        Group groupToRemove = null;

        for (Map.Entry<Group, List<User>> entry : groupMap.entrySet()) {
            List<User> groupUsers = entry.getValue();
            if (groupUsers.contains(user))
            {
                userFound = true;
                groupToRemove = entry.getKey();
                if (groupUsers.get(0).equals(user))
                {
                    throw new Exception("Cannot remove admin");
                }
                groupUsers.remove(user);
                groupSize = groupUsers.size();
                break;
            }
        }

        if (!userFound)
        {
            throw new Exception("User not found");
        }

        return groupSize + messageCnt + overallMsgCnt;

    }

    public String findMessage(Date start, Date end, int k) {

        return "Wait!!!!";
    }
}
