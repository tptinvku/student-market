package sict.apps.studentmarket.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessengerShareViewModel extends ViewModel {
    private MutableLiveData<String> objUser = new MutableLiveData<>();

    public void setObjUser(String objUser) {
        this.objUser.setValue(objUser);
    }

    public MutableLiveData<String> getObjUser() {
        return objUser;
    }
}
