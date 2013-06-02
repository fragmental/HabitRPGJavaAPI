package com.magicmicky.habitrpgmobileapp.onlineapi;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.magicmicky.habitrpgmobileapp.habits.*;


/**
 * Retrieve all the user's information from the web
 * @author MagicMicky
 * @see OnHabitsAPIResult#onUserReceived(User)
 */
public class GetUser extends WebServiceInteraction {
	private final static String command = "user/";
	/**
	 * Create a new GetUser Request, based on a callback (that will be called at the end), and a HostConfig
	 * @param callback the callback to call once we have retrieved the user
	 * @param config the Config of the server to call
	 */
	public GetUser(OnHabitsAPIResult callback, HostConfig config) {
		super(command, callback,config);
	}

	@Override
	protected Answer findAnswer(JSONObject answer) {
		return new UserData(answer, this.getCallback());
	}

	@Override
	protected HttpRequestBase getRequest() {
		return new HttpGet();
	}
	

	
	/**
	 * Parse the information about the User
	 * @author MagicMicky
	 *
	 */
	private class UserData extends Answer {
		private static final String TAG_DAILIESID = "dailyIds";
		private static final String TAG_HABITSID = "habitIds";
		private static final String TAG_TODOIDS = "todoIds";
		private static final String TAG_REWARDIDS = "rewardIds";
		private static final String TAG_STATS = "stats";
			private static final String TAG_XP = "exp";
			private static final String TAG_GP = "gp";
			private static final String TAG_HP = "hp";
			private static final String TAG_LVL="lvl";
			private static final String TAG_XP_MAX="toNextLevel";
			private static final String TAG_HP_MAX = "maxHealth";
		private static final String TAG_TASKS = "tasks";
			//private static final String TAG_TASK_TYPE = "type";
			private static final String TAG_TASK_ID = "id";
			private static final String TAG_TASK_DATE = "date";
			private static final String TAG_TASK_NOTES = "notes";
			private static final String TAG_TASK_PRIORITY = "priority";
			private static final String TAG_TASK_TEXT = "text";
			private static final String TAG_TASK_VALUE = "value";
			private static final String TAG_TASK_COMPLETED = "completed";
			private static final String TAG_TASK_UP = "up";
			private static final String TAG_TASK_DOWN = "down";
			private static final String TAG_TASK_REPEAT = "repeat";
			private static final String TAG_TASK_HISTORY="history";
				private static final String TAG_TASK_HISTORY_DATE = "date";
		private static final String TAG_AUTH = "auth";
			private static final String TAG_AUTH_LOCAL="local";
			private static final String TAG_AUTH_LOCAL_UNAME = "username";
			private static final String TAG_AUTH_FACEBOOK = "facebook";
			private static final String TAG_AUTH_FACEBOOK_DISPLAYNAME = "displayName";
		private static final String TAG_PREFS = "preferences";
			private static final String TAG_PREFS_GENDER = "gender";
			private static final String TAG_PREFS_SKIN = "skin";
			private static final String TAG_PREFS_HAIR = "hair";
			private static final String TAG_PREFS_ARMORSET = "armorSet";
			private static final String TAG_PREFS_SHOWHELM = "showHelm";
			private static final String TAG_PREFS_DAYSTART = "dayStart";
		private static final String TAG_ITEMS = "items";
			private static final String TAG_ITEMS_ARMOR = "armor";
			private static final String TAG_ITEMS_HEAD = "head";
			private static final String TAG_ITEMS_SHIELD = "shield";
			private static final String TAG_ITEMS_WEAPON = "weapon";
			//TODO:private static final String TAG_ITEMS_PETS = "pets";

		/**
		 * Create a new UserData based on a JSONObject to parse, and a callback to call
		 * @param obj the json that contains the user's values
		 * @param callback the callback to call when the information is parsed
		 */
		public UserData(JSONObject obj, OnHabitsAPIResult callback) {
			super(obj, callback);
		}


		@Override
		public void parse() {
			User user = new User();;
			try {
				this.parseHabits(user);
				this.parseUserInfos(user);
				this.parseUserLook(user);
				callback.onUserReceived(user);

			} catch (JSONException e) {
				this.callback.onError("An error happend. It might be due to a server maintenance, but please check your settings");
				e.printStackTrace();
			}
		}
		
		/**
		 * Parses the user look from the JSONObject {@link #getObject()}, and stores it in the user
		 * @param user
		 * @throws JSONException 
		 */
		private void parseUserLook(User user) throws JSONException {
				UserLook look = new UserLook();
				if(this.getObject().has(TAG_PREFS)) {
					JSONObject prefs = this.getObject().getJSONObject(TAG_PREFS);
					if(prefs.has(TAG_PREFS_GENDER))
						look.setGender(prefs.getString(TAG_PREFS_GENDER));
					if(prefs.has(TAG_PREFS_SKIN))
						look.setSkin(prefs.getString(TAG_PREFS_SKIN));
					if(prefs.has(TAG_PREFS_HAIR))
						look.setHair(prefs.getString(TAG_PREFS_HAIR));
					if(prefs.has(TAG_PREFS_ARMORSET))
						look.setArmorSet(prefs.getString(TAG_PREFS_ARMORSET));
					if(prefs.has(TAG_PREFS_SHOWHELM))
						look.setShowHelm(prefs.getBoolean(TAG_PREFS_SHOWHELM));
				}
				if(this.getObject().has(TAG_ITEMS)) {
					JSONObject items = this.getObject().getJSONObject(TAG_ITEMS);
					if(items.has(TAG_ITEMS_ARMOR))
						look.setArmor(items.getInt(TAG_ITEMS_ARMOR));
					if(items.has(TAG_ITEMS_HEAD))
						look.setHead(items.getInt(TAG_ITEMS_HEAD));
					if(items.has(TAG_ITEMS_SHIELD))
						look.setShield(items.getInt(TAG_ITEMS_SHIELD));
					if(items.has(TAG_ITEMS_WEAPON))
						look.setWeapon(items.getInt(TAG_ITEMS_WEAPON));
					user.setLook(look);
				}
		}


		/**
		 * Parse the different habits of an user
		 * @param user the user to put the information in
		 * @throws JSONException 
		 */
		private void parseHabits(User user) throws JSONException {
				List<HabitItem> items = new ArrayList<HabitItem>();
				JSONArray dailies = this.getObject().getJSONArray(TAG_DAILIESID);
				JSONObject tasks = this.getObject().getJSONObject(TAG_TASKS);
				for(int i=0;i<dailies.length();i++) {
					HabitItem it;
					JSONObject habit = tasks.getJSONObject(dailies.getString(i));
					long lastday=0;
					if(habit.has(TAG_TASK_HISTORY)) {
						JSONArray history = habit.getJSONArray(TAG_TASK_HISTORY);
						lastday = history.getJSONObject(history.length()-1).getLong(TAG_TASK_HISTORY_DATE);
					} 
					boolean[] repeats = {false,false,false,false,false,false,false};
					if(habit.has(TAG_TASK_REPEAT)) {
						JSONObject repeatTag = habit.getJSONObject(TAG_TASK_REPEAT);
						for(int j=0;j<7;j++) {
							repeats[j] = repeatTag.getBoolean(whatDay(j));
						}
					}
					it = new Daily(habit.getString(TAG_TASK_ID)
							, habit.has(TAG_TASK_NOTES) ? habit.getString(TAG_TASK_NOTES) : ""
							, habit.has(TAG_TASK_PRIORITY) ? habit.getString(TAG_TASK_PRIORITY) : "!"
							, habit.getString(TAG_TASK_TEXT)
							, habit.has(TAG_TASK_VALUE) ? habit.getDouble(TAG_TASK_VALUE) : 0
							, habit.has(TAG_TASK_COMPLETED) ? habit.getBoolean(TAG_TASK_COMPLETED) : false
							, repeats
							, lastday);
					items.add(it);
				}
				JSONArray todo = this.getObject().getJSONArray(TAG_TODOIDS);
				for(int i=0;i<todo.length();i++) {
					JSONObject habit = tasks.getJSONObject(todo.getString(i));
					HabitItem it;
					it = new ToDo(habit.getString(TAG_TASK_ID)
							, habit.has(TAG_TASK_NOTES) ? habit.getString(TAG_TASK_NOTES) : ""
							, habit.has(TAG_TASK_PRIORITY) ? habit.getString(TAG_TASK_PRIORITY) : "!"
							, habit.getString(TAG_TASK_TEXT)
							, habit.has(TAG_TASK_VALUE) ? habit.getDouble(TAG_TASK_VALUE) : 0
							, habit.has(TAG_TASK_COMPLETED) ? habit.getBoolean(TAG_TASK_COMPLETED) : false
							, habit.has(TAG_TASK_DATE) ? habit.getString(TAG_TASK_DATE) : null);
					items.add(it);
				}			
				JSONArray habitH = this.getObject().getJSONArray(TAG_HABITSID);
				for(int i=0;i<habitH.length();i++) {
					JSONObject habit= tasks.getJSONObject(habitH.getString(i));
					HabitItem it;
					it = new Habit(habit.getString(TAG_TASK_ID)
							, habit.has(TAG_TASK_NOTES) ? habit.getString(TAG_TASK_NOTES) : ""
							, habit.has(TAG_TASK_PRIORITY) ? habit.getString(TAG_TASK_PRIORITY) : "!"
							, habit.getString(TAG_TASK_TEXT)
							, habit.has(TAG_TASK_VALUE) ? habit.getDouble(TAG_TASK_VALUE) : 0
							, habit.has(TAG_TASK_UP) ? habit.getBoolean(TAG_TASK_UP) : false
							, habit.has(TAG_TASK_DOWN) ? habit.getBoolean(TAG_TASK_DOWN) : false);
					items.add(it);
				}
				JSONArray reward = this.getObject().getJSONArray(TAG_REWARDIDS);
				for(int i=0;i<reward.length();i++) {
					JSONObject habit= tasks.getJSONObject(reward.getString(i));
					HabitItem it;
					it = new Reward(habit.getString(TAG_TASK_ID)
							, habit.has(TAG_TASK_NOTES) ? habit.getString(TAG_TASK_NOTES) : ""
							, habit.has(TAG_TASK_PRIORITY) ? habit.getString(TAG_TASK_PRIORITY) : "!"
							, habit.getString(TAG_TASK_TEXT)
							, habit.has(TAG_TASK_VALUE) ? habit.getDouble(TAG_TASK_VALUE) : 0);
					items.add(it);
				}
				user.setItems(items);

			
		}

		/**
		 * Parse the information of an user
		 * @param user the user to put the information in
		 * @throws JSONException 
		 */
		private void parseUserInfos(User user) throws JSONException {
			if(this.getObject().has(TAG_STATS)) {
				JSONObject stats = this.getObject().getJSONObject(TAG_STATS);
				if(stats.has(TAG_LVL))
					user.setLvl(stats.getInt(TAG_LVL));
				if(stats.has(TAG_XP))
					user.setXp(stats.getDouble(TAG_XP));
				if(stats.has(TAG_XP_MAX))
					user.setMaxXp(stats.getDouble(TAG_XP_MAX));
				if(stats.has(TAG_HP))
					user.setHp(stats.getDouble(TAG_HP));
				if(stats.has(TAG_HP_MAX))
					user.setMaxHp(stats.getDouble(TAG_HP_MAX));
				if(stats.has(TAG_GP))
					user.setGp(stats.getDouble(TAG_GP));
			}
			if(this.getObject().has(TAG_PREFS)) {
			JSONObject prefs = this.getObject().getJSONObject(TAG_PREFS);
			if(prefs.has(TAG_PREFS_DAYSTART))
				user.setDayStart(prefs.getInt(TAG_PREFS_DAYSTART));
			else
				user.setDayStart(0);
			}
			if(this.getObject().has(TAG_AUTH)) {
				JSONObject auth = this.getObject().getJSONObject(TAG_AUTH);
				if(auth.has(TAG_AUTH_LOCAL)) {
					user.setName(auth.getJSONObject(TAG_AUTH_LOCAL).getString(TAG_AUTH_LOCAL_UNAME));
				} else if(auth.has(TAG_AUTH_FACEBOOK)) {
					user.setName(auth.getJSONObject(TAG_AUTH_FACEBOOK).getString(TAG_AUTH_FACEBOOK_DISPLAYNAME));
				}
			}
			
		}
		/**
		 * returns the day string depending on the number
		 * @param j the number
		 * @return the first/two first letter of the day
		 */
		private String whatDay(int j) {
			if(j==0)
				return "m";
			else if(j==1)
				return "t";
			else if(j==2)
				return "w";
			else if(j==3)
				return "th";
			else if(j==4)
				return "f";
			else if(j==5)
				return "s";
			else if(j==6)
				return "su";
			return "su";
		}
		
		
	}
}
