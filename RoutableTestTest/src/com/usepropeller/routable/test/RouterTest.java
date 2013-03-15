package com.usepropeller.routable.test;

import java.util.Map;

import com.usepropeller.routable.Router;

import junit.framework.Assert;

import android.app.ListActivity;
import android.content.Intent;
import android.test.AndroidTestCase;

public class RouterTest extends AndroidTestCase {
	private boolean _called;

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

		try {
			router.intentFor("users/4");
		    fail("Invalid route did not throw exception");
		} catch (Router.RouteNotFoundException e) {
			return;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Incorrect exception throw: " + e.toString());
		}

		fail("Invalid route did not throw exception");
	}

	public void test_invalid_context() {
		Router router = new Router();
		router.map("users", ListActivity.class);

		try {
			router.open("users");
		    fail("Invalid context did not throw exception");
		} catch (Router.ContextNotProvided e) {
			return;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Incorrect exception throw: " + e.toString());
		}

		fail("Invalid context did not throw exception");
	}

	public void test_code_callbacks() {
		Router router = new Router();
		router.map("callback", new Router.RouterCallback() {
			@Override
			public void run(Map<String, String> params) {
				RouterTest.this._called = true;
			}
		});

		router.open("callback");

		Assert.assertTrue(this._called);
	}

	public void test_code_callbacks_with_params() {
		Router router = new Router();
		router.map("callback/:id", new Router.RouterCallback() {
			@Override
			public void run(Map<String, String> params) {
				RouterTest.this._called = true;
				Assert.assertEquals("123", params.get("id"));
			}
		});

		router.open("callback/123");

		Assert.assertTrue(this._called);
	}
}
