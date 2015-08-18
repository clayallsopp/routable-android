# Routable

Routable is an in-app native URL router, for Android. Also available for [iOS](https://github.com/usepropeller/routable-ios).

## Usage

Set up your app's router and URLs:

```java
import com.usepropeller.routable.Router;

public class PropellerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Symbol-esque params are passed as intent extras to the activities
        Router.sharedRouter().map("users/:id", UserActivity.class);

        // Symbol-esque params are passed as intent extras to the activities
        // And, we support typed parameter
        // i -> Integer
        // l -> Long
        // f -> Float
        // d -> Double
        // s -> String
        Router.sharedRouter().map("users/info/i:id/s:name", NewUserActivity.class);
    }
}
```

In your `Activity` classes, add support for the URL params:

```java
import com.usepropeller.routable.Router;

public class UserActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle intentExtras = getIntent().getExtras();
        // Note this extra, and how it corresponds to the ":id" above
        String userId = intentExtras.get("id");
    }
}

public class NewUserActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle intentExtras = getIntent().getExtras();
        // Corresponds to the "i:id" above
        int id = intentExtras.get("id");
        // Corresponds to the "s:name" above
        String name = intentExtras.get("name");
    }
}
```

*Anywhere* else in your app, open some URLs:

```java
// starts a new UserActivity
Router.sharedRouter().open("users/16", activityContext);
// starts a new NewUserActivity
Router.sharedRouter().open("users/new/7/walfud", activityContext);
```

## Installation

Routable is currently an Android library project (so no Maven).

If you're in a hurry, you can just copy-paste the [Router.java](https://github.com/usepropeller/routable-android/blob/master/src/com/usepropeller/routable/Router.java) file.

Or if you're being a little more proactive, you should import the Routable project (this entire git repo) into Eclipse and [reference it](http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject) in your own project. 

## Features

### Routable Functions

You can call arbitrary blocks of code with Routable:

```java
Router.sharedRouter().map("logout", new Router.RouterCallback() {
    public void run(Router.RouteContext context) {
        User.logout();
    }
});

// Somewhere else
Router.sharedRouter().open("logout");
```

### Open External URLs

Sometimes you want to open a URL outside of your app, like a YouTube URL or open a web URL in the browser. You can use Routable to do that:

```java
Router.sharedRouter().openExternal("http://www.youtube.com/watch?v=oHg5SJYRHA0")
```

### Multiple Routers

If you need to use multiple routers, simply create new instances of `Router`:

```java
Router adminRouter = new Router();

Router userRouter = new Router();
```

## Contact

Clay Allsopp ([http://clayallsopp.com](http://clayallsopp.com))

- [http://twitter.com/clayallsopp](http://twitter.com/clayallsopp)
- [clay@usepropeller.com](clay@usepropeller.com)

walfud

- [http://diordna.sinaapp.com/](http://diordna.sinaapp.com/)
- [walfud@aliyun.com](walfud@aliyun.com)

## License

Routable for Android is available under the MIT license. See the LICENSE file for more info.
