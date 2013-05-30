package com.magicmicky.habitrpgmobileapp.onlineapi;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.magicmicky.habitrpgmobileapp.habits.Daily;
import com.magicmicky.habitrpgmobileapp.habits.Habit;
import com.magicmicky.habitrpgmobileapp.habits.HabitItem;
import com.magicmicky.habitrpgmobileapp.habits.HabitType;
import com.magicmicky.habitrpgmobileapp.habits.Reward;
import com.magicmicky.habitrpgmobileapp.habits.ToDo;

public class PostTask extends WebServiceInteraction {
	private static final String CMD = "user/task/";
	private HabitItem habit;
	public PostTask(OnHabitsAPIResult callback, HostConfig config, HabitItem habit) {
		super(CMD, callback, config);
		this.habit=habit;
	}

	@Override
	protected HttpRequestBase getRequest() {
		HttpPost method =  new HttpPost();
		try {
			StringEntity ent = new StringEntity(habit.getJSONString());
			ent.setContentType("application/json");
			method.setEntity(ent);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return method;
	}

	@Override
	protected Answer findAnswer(JSONObject answer) {
		return new PostTaskData(answer, this.getCallback());
	}


	private class PostTaskData extends Answer {
		private static final String TAG_ERR ="err";
		private static final String TAG_TASK_TYPE = "type";
		private static final String TAG_TASK_ID = "id";
		private static final String TAG_TASK_NOTES = "notes";
		private static final String TAG_TASK_TEXT = "text";
		private static final String TAG_TASK_VALUE = "value";
		private static final String TAG_TASK_COMPLETED = "completed";
		private static final String TAG_TASK_UP = "up";
		private static final String TAG_TASK_DOWN = "down";
		public PostTaskData(JSONObject obj, OnHabitsAPIResult callback) {
			super(obj, callback);
			this.setObject(obj);
		}

		@Override
		public void parse() {
			if(this.getObject().has(TAG_ERR)) {
				
				try {
					this.callback.onError(this.getObject().getString(TAG_ERR));
				} catch (JSONException e) {
					e.printStackTrace();
					this.callback.onError("An error happend. It might be due to a server maintenance, but please check your settings");
				}
				return;
			}
			HabitItem item  = this.parseTask();
			if(item == null) {
				this.callback.onError("item==null");
			}
			if(this.callback != null)
				this.callback.onPostTaskAnswer(item);
		}

		private HabitItem parseTask() {
			JSONObject obj = this.getObject();
			String type;
			HabitItem item = null;
			try {
				type = obj.getString(TAG_TASK_TYPE);

				if(type.equals(HabitType.daily.toString())) {
					boolean[] days = {false,false,false,false,false,false,false};
					item = new Daily(obj.getString(TAG_TASK_ID), obj.has(TAG_TASK_NOTES) ? obj.getString(TAG_TASK_NOTES) : "", "", obj.getString(TAG_TASK_TEXT), obj.getDouble(TAG_TASK_VALUE), obj.getBoolean(TAG_TASK_COMPLETED),days);
				} else if(type.equals(HabitType.todo.toString())) {
					item = new ToDo(obj.getString(TAG_TASK_ID), obj.has(TAG_TASK_NOTES) ? obj.getString(TAG_TASK_NOTES) : "", "", obj.getString(TAG_TASK_TEXT), obj.getDouble(TAG_TASK_VALUE), obj.getBoolean(TAG_TASK_COMPLETED), null);
				} else if(type.equals(HabitType.reward.toString())) {
					item = new Reward(obj.getString(TAG_TASK_ID), obj.has(TAG_TASK_NOTES) ? obj.getString(TAG_TASK_NOTES) : "", "", obj.getString(TAG_TASK_TEXT), obj.getDouble(TAG_TASK_VALUE));
				} else {
					item = new Habit(obj.getString(TAG_TASK_ID), obj.has(TAG_TASK_NOTES) ? obj.getString(TAG_TASK_NOTES) : "", "", obj.getString(TAG_TASK_TEXT), obj.getDouble(TAG_TASK_VALUE), obj.getBoolean(TAG_TASK_UP), obj.getBoolean(TAG_TASK_DOWN));
				}
			} catch (JSONException e) {
				this.callback.onError("An error happend. It might be due to a server maintenance, but please check your settings");
				e.printStackTrace();
			}
			return item;	
		}
		
	}

}
