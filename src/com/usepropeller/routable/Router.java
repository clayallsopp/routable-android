package com.usepropeller.routable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Router {
	private static final Router _router = new Router();

	public static Router sharedRouter() {
		return _router;
	}

	public static abstract class RouterCallback {
		public abstract void run(Map<String, String> params);
	}

	public static class RouterOptions {
		Class _klass;
		RouterCallback _callback;
		Map<String, String> _defaultParams;

		public RouterOptions() {

		}

		public RouterOptions(Class klass) {
			this._klass = klass;
		}

		public RouterOptions(Map<String, String> defaultParams) {
			this._defaultParams = defaultParams;
		}

		public RouterOptions(Map<String, String> defaultParams, Class klass) {
			this._defaultParams = defaultParams;
			this._klass = klass;
		}

		public void setOpenClass(Class klass) {
			this._klass = klass;
		}

		public Class getOpenClass() {
			return this._klass;
		}

		public RouterCallback getCallback() {
			return this._callback;
		}

		public void setCallback(RouterCallback callback) {
			this._callback = callback;
		}

		public void setDefaultParams(Map<String, String> defaultParams) {
			this._defaultParams = defaultParams;
		}

		public Map<String, String> getDefaultParams() {
			return this._defaultParams;
		}
	}

	public static class RouterParams {
		public RouterOptions routerOptions;
		public Map<String, String> openParams;
	}

	private final Map<String, RouterOptions> _routes = new HashMap<String, RouterOptions>();
	private final Map<String, RouterParams> _cachedRoutes = new HashMap<String, RouterParams>();
	private Context _context;

	public Router() {

	}

	public Router(Context context) {
		this.setContext(context);
	}

	public void setContext(Context context) {
		this._context = context;
	}

	public void map(String format, RouterCallback callback) {
		RouterOptions options = new RouterOptions();
		options.setCallback(callback);
		this.map(format, null, options);
	}

	public void map(String format, Class klass) {
		this.map(format, klass, null);
	}

	public void map(String format, Class klass, RouterOptions options) {
		if (options == null) {
			options = new RouterOptions();
		}
		options.setOpenClass(klass);
		this._routes.put(format, options);
	}

	public void openExternal(String url) {
		this.openExternal(url, this._context);
	}

	public void openExternal(String url, Context context) {
		this.openExternal(url, null, this._context);
	}

	public void openExternal(String url, Bundle extras) {
		this.openExternal(url, extras, this._context);
	}

	public void openExternal(String url, Bundle extras, Context context) {
		if (context == null) {
			throw new ContextNotProvided(
					"You need to supply a context for Router "
							+ this.toString());
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		this.addFlagsToIntent(intent, context);
		if (extras != null) {
			intent.putExtras(extras);
		}
		context.startActivity(intent);
	}

	public void open(String url) {
		this.open(url, this._context);
	}

	public void open(String url, Bundle extras) {
		this.open(url, extras, this._context);
	}

	public void open(String url, Context context) {
		this.open(url, null, this._context);
	}

	public void open(String url, Bundle extras, Context context) {
		RouterParams params = this.paramsForUrl(url);
		RouterOptions options = params.routerOptions;
		if (options.getCallback() != null) {
			options.getCallback().run(params.openParams);
			return;
		}

		if (context == null) {
			throw new ContextNotProvided(
					"You need to supply a context for Router "
							+ this.toString());
		}
		Intent intent = this.intentFor(url);
		intent.setClass(context, options.getOpenClass());
		this.addFlagsToIntent(intent, context);
		if (extras != null) {
			intent.putExtras(extras);
		}
		context.startActivity(intent);
	}

	private void addFlagsToIntent(Intent intent, Context context) {
		if (context == this._context) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
	}

	public Intent intentFor(String url) {
		RouterParams params = this.paramsForUrl(url);
		RouterOptions options = params.routerOptions;
		Intent intent = new Intent();
		if (options.getDefaultParams() != null) {
			for (Entry<String, String> entry : options.getDefaultParams().entrySet()) {
				intent.putExtra(entry.getKey(), entry.getValue());
			}
		}
		for (Entry<String, String> entry : params.openParams.entrySet()) {
			intent.putExtra(entry.getKey(), entry.getValue());
		}
		return intent;
	}

	// Take a url (i.e. "/users/16/hello"
	private RouterParams paramsForUrl(String url) {
		if (this._cachedRoutes.get(url) != null) {
			return this._cachedRoutes.get(url);
		}

		String[] parts = url.split("/");

		RouterOptions openOptions = null;
		RouterParams openParams = null;
		for (Entry<String, RouterOptions> entry : this._routes.entrySet()) {
			String format = entry.getKey();
			RouterOptions options = entry.getValue();
			String[] formatParts = format.split("/");

			if (formatParts.length != parts.length) {
				continue;
			}

			boolean matched = true;
			Map<String, String> formatParams = new HashMap<String, String>();
			for (int index = 0; index < formatParts.length; index++) {
				String formatPart = formatParts[index];
				String checkPart = parts[index];

				if (formatPart.charAt(0) == ':') {
					String key = formatPart.substring(1, formatPart.length());
					formatParams.put(key, checkPart);
					continue;
				}

				if (!formatPart.equals(checkPart)) {
					matched = false;
					break;
				}
			}

			if (!matched) {
				continue;
			}

			openOptions = options;
			openParams = new RouterParams();
			openParams.openParams = formatParams;
			openParams.routerOptions = options;
			break;
		}

		if (openOptions == null || openParams == null) {
			throw new RouteNotFoundException("No route found for url " + url);
		}

		this._cachedRoutes.put(url, openParams);
		return openParams;
	}

	public static class RouteNotFoundException extends RuntimeException {
		private static final long serialVersionUID = -2278644339983544651L;

		public RouteNotFoundException(String message) {
			super(message);
		}
	}

	public static class ContextNotProvided extends RuntimeException {
		private static final long serialVersionUID = -1381427067387547157L;

		public ContextNotProvided(String message) {
			super(message);
		}
	}
}
