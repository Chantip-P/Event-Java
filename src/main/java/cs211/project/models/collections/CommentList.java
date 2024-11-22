package cs211.project.models.collections;

import cs211.project.models.Comment;

import java.util.ArrayList;
import java.util.Iterator;

public class CommentList implements Iterable<Comment> {
    private ArrayList<Comment> comments;
    public CommentList() {
        comments = new ArrayList<>();
    }

    public void addComment(Comment comment){
        comments.add(comment);
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    @Override
    public Iterator<Comment> iterator() { return comments.iterator(); }

}
