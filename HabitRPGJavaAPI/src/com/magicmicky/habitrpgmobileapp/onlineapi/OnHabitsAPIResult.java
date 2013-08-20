package com.magicmicky.habitrpgmobileapp.onlineapi;

import com.magicmicky.habitrpgmobileapp.habits.HabitItem;
import com.magicmicky.habitrpgmobileapp.habits.User;
/**
 * This is the callback interface. The functions here are called by the different POST, PUT, GET, and DELETE
 * methods, and notifies the application of changes
 * @author MagicMicky
 *
 */
public interface OnHabitsAPIResult {
	/**
	 * This is the function called once a user have been updated
	 * @param user
	 */
	public void onUserReceived(User user);
	/**
	 * This is the callback called when we have a result from an update Tasks
	 * @param xp
	 * @param hp
	 * @param gold
	 * @param lvl
	 * @param delta
	 */
	void onPostResult(double xp, double hp, double gold, double lvl,
			double delta);
	/**
	 * This is called before starting every actions
	 */
	void onPreResult();
	/**
	 * This is called when there has been an error from the API
	 * @param message an optional error message
	 */
	void onError(WebServiceInteraction.WebServiceException error);
	/**
	 * This is called when a new Task is added/updated
	 * @param task
	 */
	void onPostTaskAnswer(HabitItem task);

	/**
	 * This is called when a task have been deleted
	 * @param deletedTask the deleted task
	 */
	void onDeletedTask(HabitItem deletedTask);
	
	/**
	 * Answer gotten agter a task have been edited
	 * @param item
	 */
	public void onEditTaskAnswer(HabitItem item);
}
