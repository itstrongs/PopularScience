package com.itstrong.popularscience.fragment.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;

/**
 * 竞赛页面
 */
public class CompetitionFragment extends BaseFragment implements View.OnClickListener {

	private View contestGame;
	private View layoutCompetition;
	private View layoutAnswer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_contest, container, false);
		return mContentView;
	}

	@Override
	public void findViewById() {
		contestGame = mContentView.findViewById(R.id.layout_contest_game);
		layoutCompetition = mContentView.findViewById(R.id.layout_know_competition);
		layoutAnswer = mContentView.findViewById(R.id.layout_day_answer);
	}

	@Override
	public void setListener() {
		contestGame.setOnClickListener(this);
		layoutCompetition.setOnClickListener(this);
		layoutAnswer.setOnClickListener(this);
	}

	@Override
	public void processLogic() {
		mActivity.setFragmentTitle("科普房山");
		mActivity.setBtnBackIsInvisible(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.layout_contest_game:
				mActivity.switchFragmentPage(mActivity.FRAGMENT_INTERACT_GAME);
				break;
			case R.id.layout_know_competition:
				mActivity.switchFragmentPage(mActivity.FRAGMENT_KNOW_COMPETITION);
				break;
			case R.id.layout_day_answer:
				mActivity.switchFragmentPage(mActivity.FRAGMENT_DAY_ANSWER);
				break;
		}
	}
}
