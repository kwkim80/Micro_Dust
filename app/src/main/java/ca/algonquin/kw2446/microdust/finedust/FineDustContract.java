package ca.algonquin.kw2446.microdust.finedust;

import ca.algonquin.kw2446.microdust.model.FineDust;

public class FineDustContract {

    public interface View {
        void showFineDustResult(FineDust fineDust);

        void showLoadError(String message);

        void loadingStart();

        void loadingEnd();

        void newLoad(double lat, double lng);

        void loadFineDust();
    }

    interface UserActionsListener {
        void loadFineDustData();
    }
}
