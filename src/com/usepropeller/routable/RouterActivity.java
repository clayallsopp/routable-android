package com.usepropeller.routable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class RouterActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    Intent intent = getIntent();
	    String url;

	    Bundle extras = intent.getExtras();
	    if (extras.containsKey("url")) {
	    	url = extras.getString("url");
	    }
	    else {
		    Uri data = intent.getData();
		    url = data.getPath();
		    if (url.startsWith("/")) {
		    	url = url.substring(1);
		    }
		    if (Router.sharedRouter().getRootUrl() != null) {
			    Router.sharedRouter().open(Router.sharedRouter().getRootUrl());
		    }
	    }

	    Router.sharedRouter().open(url);

	    setResult( RESULT_OK, null );
	    finish();
	}
}
