package ca.algonquin.kw2446.microdust.data;

import ca.algonquin.kw2446.microdust.model.FineDust;
import retrofit2.Callback;

public interface FineDustRepository {

    boolean isAvailable();

    void getFineDustData(Callback<FineDust> callback);
}
