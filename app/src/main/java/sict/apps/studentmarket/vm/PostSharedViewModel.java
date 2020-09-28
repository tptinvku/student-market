package sict.apps.studentmarket.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.List;

import sict.apps.studentmarket.models.Post;

public class PostSharedViewModel extends ViewModel implements Serializable {
    private MutableLiveData<String> path = new MutableLiveData<>();
    private MutableLiveData<List<String>> pathImgs = new MutableLiveData<>();
    private MutableLiveData<Post> post = new MutableLiveData<>();
    private MutableLiveData<String> objUser = new MutableLiveData<>();

    public void setObjUser(String objUser) {
        this.objUser.setValue(objUser);
    }

    public MutableLiveData<String> getObjUser() {
        return objUser;
    }
    public MutableLiveData<String> getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path.setValue(path);
    }

    public MutableLiveData<Post> getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post.setValue(post);
    }

    public MutableLiveData<List<String>> getPathImgs() {
        return pathImgs;
    }

    public void setPathImgs(List<String> pathImgs) {
        this.pathImgs.setValue(pathImgs);
    }


}
