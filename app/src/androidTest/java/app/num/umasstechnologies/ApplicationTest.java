package app.num.umasstechnologies;

import android.app.Application;
import android.test.ApplicationTestCase;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {

        super(Application.class);

        RealmConfiguration configuration = new RealmConfiguration.Builder(getContext()).build();
        Realm.setDefaultConfiguration(configuration);

    }
}