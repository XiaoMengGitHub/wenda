package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Comment;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by nowcoder on 2016/7/10.
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDAO.selectCommentByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment)>0 ? comment.getId() :0;
    }
    public int getCommentCount(int entityId, int entityType){


        return commentDAO.getCommentCount(entityId,entityType);
    }


        public void deleteComment(int entityId, int entityType) {
            commentDAO.updateStatus(entityId, entityType, 1);
        }

        public  Comment getCommentById(int id){
        return  commentDAO.getCommentById(id);
        }
}
