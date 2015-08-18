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


	/**
	 * Testing cases for typed parameter
	 */
	public void test_basic_with_type() {
		{	// Testing case for int value
			Router router = new Router();
			router.map("users/i:user_id", ListActivity.class);
			Assert.assertEquals(4, router.intentFor("users/4").getExtras().getInt("user_id"));
		}
		{	// Testing case for long value
			Router router = new Router();
			router.map("users/l:user_id", ListActivity.class);
			Assert.assertEquals(4L, router.intentFor("users/4").getExtras().getLong("user_id"));
		}
		{	// Testing case for float value
			Router router = new Router();
			router.map("users/f:user_id", ListActivity.class);
			Assert.assertEquals(4.5f, router.intentFor("users/4.5").getExtras().getFloat("user_id"));
		}
		{	// Testing case for double value
			Router router = new Router();
			router.map("users/d:user_id", ListActivity.class);
			Assert.assertEquals(4.5, router.intentFor("users/4.5").getExtras().getDouble("user_id"));
		}
		{	// Testing case for string value
			Router router = new Router();
			router.map("users/s:user_id", ListActivity.class);
			Assert.assertEquals("text", router.intentFor("users/text").getExtras().getString("user_id"));
		}
	}


	/**
	 * WARNING: Because the original constructor `RouteContext` only accept `Map<String, String>` as
	 * its param, so I can ONLY SUPPORT STRING TYPE!!! Parameter with other type will be ignored.
	 */
	public void test_code_callbacks_with_typed_params() {
		Router router = new Router(this.getContext());
		router.map("callback/:defaultType/s:supportType/i:invalidType", new Router.RouterCallback() {
			@Override
			public void run(Router.RouteContext context) {
				RouterTest.this._called = true;
				Assert.assertEquals("ok", context.getParams().get("defaultType"));
				Assert.assertEquals("alsoOk", context.getParams().get("supportType"));
				Assert.assertEquals(null, context.getParams().get("invalid"));
			}
		});

		router.open("callback/ok/alsoOk/invalid");

		Assert.assertTrue(this._called);
	}


	public void test_url_querystring_with_typed_param() {
		Router router = new Router();
		router.map("/users/:defaultStringValue/i:intValue/l:longValue/f:floatValue/d:doubleValue/s:stringValue", ListActivity.class);

		Intent intent = router.intentFor("/users/string/1/2/3.0/4.0/anotherString/?key1=val2");
		Bundle extras = intent.getExtras();

		Assert.assertEquals("string", extras.getString("defaultStringValue"));
		Assert.assertEquals(1, extras.getInt("intValue"));
		Assert.assertEquals(2L, extras.getLong("longValue"));
		Assert.assertEquals(3.0f, extras.getFloat("floatValue"));
		Assert.assertEquals(4.0, extras.getDouble("doubleValue"));
		Assert.assertEquals("anotherString", extras.getString("stringValue"));
		Assert.assertEquals("val2", extras.getString("key1"));
	}

}
