package com.usepropeller.routable.test;

import java.util.Map;

import com.usepropeller.routable.Router;

import junit.framework.Assert;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;

public class RouterTest extends AndroidTestCase {
	private boolean _called;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		this._called = false;
	}

	public void test_basic() {
		Router router = new Router();
		router.map("users/:user_id", ListActivity.class);

		Intent intent = router.intentFor("users/4");
		Assert.assertEquals("4", intent.getExtras().getString("user_id"));
	}

	public void test_empty() {
		Router router = new Router();
		router.map("users", ListActivity.class);

		Intent intent = router.intentFor("users");
		Assert.assertNull(intent.getExtras());
	}

	public void test_invalid_route() {
		Router router = new Router();
		boolean exceptionThrown = false;

		try {
			router.intentFor("users/4");
		} catch (Router.RouteNotFoundException e) {
			exceptionThrown = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Incorrect exception throw: " + e.toString());
		}

		Assert.assertTrue("Invalid route did not throw exception", exceptionThrown);
	}

	public void test_invalid_context() {
		Router router = new Router();
		router.map("users", ListActivity.class);
		boolean exceptionThrown = false;

		try {
			router.open("users");
		} catch (Router.ContextNotProvided e) {
			exceptionThrown = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Incorrect exception throw: " + e.toString());
		}

		Assert.assertTrue("Invalid context did not throw exception", exceptionThrown);
	}

	public void test_code_callbacks() {
		Router router = new Router(this.getContext());
		router.map("callback", new Router.RouterCallback() {
			@Override
			public void run(Router.RouteContext context) {
				RouterTest.this._called = true;

                Assert.assertNotNull(context.getContext());
			}
		});

		router.open("callback");

		Assert.assertTrue(this._called);
	}

	public void test_code_callbacks_with_params() {
		Router router = new Router(this.getContext());
		router.map("callback/:id", new Router.RouterCallback() {
			@Override
			public void run(Router.RouteContext context) {
				RouterTest.this._called = true;
				Assert.assertEquals("123", context.getParams().get("id"));
			}
		});

		router.open("callback/123");

		Assert.assertTrue(this._called);
	}

	public void test_code_callbacks_with_extras() {
		Router router = new Router(this.getContext());
		router.map("callback/:id", new Router.RouterCallback() {
            @Override
            public void run(Router.RouteContext context) {
                RouterTest.this._called = true;
                Assert.assertEquals("value", context.getExtras().getString("test"));
            }
        });

        Bundle extras = new Bundle();
        extras.putString("test", "value");

		router.open("callback/123", extras);

		Assert.assertTrue(this._called);
	}

    public void test_url_starting_with_slash() {
        Router router = new Router();
        router.map("/users", ListActivity.class);

        Intent intent = router.intentFor("/users");
        Assert.assertNull(intent.getExtras());
    }

    public void test_url_querystring() {
        Router router = new Router();
        router.map("/users/:id", ListActivity.class);

        Intent intent = router.intentFor("/users/123?key1=val2");
        Bundle extras = intent.getExtras();

        Assert.assertEquals("123", extras.getString("id"));
        Assert.assertEquals("val2", extras.getString("key1"));
    }

    public void test_url_containing_spaces() {
        Router router = new Router();
        router.map("/path+entry/:id", ListActivity.class);

        Intent intent = router.intentFor("/path+entry/123");
        Bundle extras = intent.getExtras();

        Assert.assertEquals("123", extras.getString("id"));
    }

    public void test_url_querystring_with_encoded_value() {
        Router router = new Router();
        router.map("/users/:id", ListActivity.class);

        Intent intent = router.intentFor("/users/123?key1=val+1&key2=val%202");
        Bundle extras = intent.getExtras();

        Assert.assertEquals("val 1", extras.getString("key1"));
        Assert.assertEquals("val 2", extras.getString("key2"));
    }

    public void test_url_querystring_without_value() {
        Router router = new Router();
        router.map("/users/:id", ListActivity.class);

        Intent intent = router.intentFor("/users/123?val1");
        Bundle extras = intent.getExtras();

        Assert.assertTrue(extras.containsKey("val1"));
    }

    public void test_url_starting_with_slash_with_params() {
        Router router = new Router();
        router.map("/users/:user_id", ListActivity.class);

        Intent intent = router.intentFor("/users/4");
        Assert.assertEquals("4", intent.getExtras().getString("user_id"));
    }
}
