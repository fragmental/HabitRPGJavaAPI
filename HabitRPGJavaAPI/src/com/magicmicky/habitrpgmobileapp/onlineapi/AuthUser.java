package com.magicmicky.habitrpgmobileapp.onlineapi;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.magicmicky.habitrpgmobileapp.onlineapi.WebServiceInteraction.Answer;
import com.magicmicky.habitrpgmobileapp.onlineapi.WebServiceInteraction.WebServiceException;

public class AuthUser extends WebServiceInteraction {
	private final static String CMD="user/auth/local";
	private final static String CMD_FB="user/auth/facebook";
	private static final String TAG_USER_NAME = "username";
	private static final String TAG_USER_PASSWORD = "password";
	private static final String TAG_FACEBOOK_ID="facebook_id";
	private String mUserName;
	private String mUserPassword;
	private String mUserFacebookId;
	public AuthUser(OnHabitsAPIResult callback,HostConfig config, String name, String password) {
		super(CMD, callback, config);
		this.mUserName= name;
		this.mUserPassword=password;
	}
	public AuthUser(OnHabitsAPIResult callback,HostConfig config, String facebook_id) {
		super(CMD_FB, callback, config);
		this.mUserFacebookId= facebook_id;
	}
	protected HttpRequestBase getRequest() {
		HttpPost method =  new HttpPost();
		try {
			StringBuilder sb = new StringBuilder()
			.append("{");
			if(mUserFacebookId==null) {
				sb.append("\"").append(TAG_USER_NAME).append("\":").append(JSONObject.quote(mUserName)).append(",")
				.append("\"").append(TAG_USER_PASSWORD).append("\":").append(JSONObject.quote(mUserPassword));
			} else {
				sb.append("\"").append(TAG_FACEBOOK_ID).append("\":").append(JSONObject.quote(mUserFacebookId));
			}
			sb.append("}");
			StringEntity ent = new StringEntity(sb.toString());
			ent.setContentType("application/json");
			method.setEntity(ent);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return method;
	}

	@Override
	protected Answer findAnswer(JSONObject answer) {
		return new AuthUserData(answer, getCallback());
	}

	public class AuthUserData extends Answer {
		private final static String TAG_API_TOKEN="token";
		private final static String TAG_API_USER_TOKEN="id";
		private final static String TAG_ERR="err";
		
		public AuthUserData(JSONObject obj, OnHabitsAPIResult callback) {
			super(obj,callback);
		}
		
		public void parse() {
			JSONObject obj = this.getObject();
			String api_t;
			String user_t;
			try {
				api_t = obj.getString(TAG_API_TOKEN);
				user_t = obj.getString(TAG_API_USER_TOKEN);
				callback.onUserConnected(api_t, user_t);
			} catch (JSONException e) {
				if(obj.has(TAG_ERR)) {
					try {
						callback.onError((new WebServiceException(WebServiceException.HABITRPG_REGISTRATION_ERROR, obj.getString(TAG_ERR))));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				callback.onError((new WebServiceException(WebServiceException.HABITRPG_REGISTRATION_ERROR)));
			}
		}
	}

}
