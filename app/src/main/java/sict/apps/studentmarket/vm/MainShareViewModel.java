package sict.apps.studentmarket.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.nkzawa.socketio.client.Socket;

import java.util.List;

import sict.apps.studentmarket.models.Post;

public class MainShareViewModel extends ViewModel {
    private MutableLiveData<String> objUser = new MutableLiveData<>();
    private MutableLiveData<Socket> mSocket = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();

    public MutableLiveData<List<Post>> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts.setValue(posts);
    }

    public void setObjUser(String objUser) {
        this.objUser.setValue(objUser);
    }

    public MutableLiveData<String> getObjUser() {
        return objUser;
    }

    public void setmSocket(Socket mSocket) {
        this.mSocket.setValue(mSocket);
    }

    public MutableLiveData<Socket> getmSocket() {
        return mSocket;
    }
}
