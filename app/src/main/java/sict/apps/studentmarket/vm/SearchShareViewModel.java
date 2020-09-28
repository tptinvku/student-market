package sict.apps.studentmarket.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import sict.apps.studentmarket.models.Result;


public class SearchShareViewModel extends ViewModel {
    private MutableLiveData<List<Result>> results = new MutableLiveData<>();

    public void setResults(List<Result> results) {
        this.results.setValue(results);
    }

    public MutableLiveData<List<Result>> getResults() {
        return results;
    }
}
