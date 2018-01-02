package com.itstrong.popularscience.utils;

import android.os.Environment;

import java.io.File;

public class ConstantHolder {

	/** 系统颜色 */
	public static final String APP_COLOR_STYLE = "#113DEE";

	/** 本地缓存路径 */
	public static final String PATH_CACHE = Environment.getExternalStorageDirectory().getPath() + File.separator + "PopularScience";
	/** 本地缓存图片路径 */
	public static final String PATH_CACHE_IMG = PATH_CACHE + File.separator + "image" + File.separator;
	/** 本地缓存视频路径 */
	public static final String PATH_CACHE_VIDEO = PATH_CACHE + File.separator + "video" + File.separator;
	/** 本地缓存图书路径 */
	public static final String PATH_CACHE_BOOK = PATH_CACHE + File.separator + "book" + File.separator;

	/** 服务器地址 */
	public static final String URL_HOST = "http://120.27.37.22:8080/fang/";
	/** 互动游戏 */
	public static final String URL_INTERACTIVE_GAME = URL_HOST + "fang/getInteractiveGame.do";
	/** 知识竞赛 */
	public static final String URL_KNOWLEDGE_CONTEST = URL_HOST + "fang/getKnoCompetition.do";
	/** 提交积分 */
	public static final String URL_SUBMIT_SCORE = URL_HOST + "fang/submitScore.do";
	/** 每日一答 */
	public static final String URL_DAY_ANSWER = URL_HOST + "fang/getInterlocution.do";
	/** 获取排名 */
	public static final String URL_GET_RANKING = URL_HOST + "fang/getRanking.do";
	/** 我的收藏 */
	public static final String URL_MY_COLLECT = URL_HOST + "fang/getMyFavorite.do";
	/** 添加收藏 */
	public static final String URL_ADD_COLLECT = URL_HOST + "fang/addMyFavorite.do";
	/** 删除收藏 */
	public static final String URL_DELETE_COLLECT = URL_HOST + "fang/deleteMyFavorite.do";
	/** 用户注册 */
	public static final String URL_USER_REGISTER = URL_HOST + "fang/registerUser.do";
	/** 用户登录 */
	public static final String URL_USER_LOGIN = URL_HOST + "fang/login.do";
	/** 账号设置 */
	public static final String URL_USER_SETTING = URL_HOST + "fang/settings.do";
	/** 意见反馈 */
	public static final String URL_USER_FEEDBACK = URL_HOST + "fang/feedback.do";
	/** 科普图书下载详情 */
	public static final String URL_BOOK_DOWNLOAD = URL_HOST + "fang/downloadSciBookContent.do";
	/** 科普图书列表详情 */
	public static final String URL_BOOK_LIST = URL_HOST + "fang/getSciBookList.do";
	/** 视频下载 */
	public static final String URL_VIDEO_DOWNLOAD = URL_HOST + "fang/downloadSciVideo.do";
	/** 科普发现中的内容详情 */
	public static final String URL_DISCOVER_CONTENT = URL_HOST + "fang/getSciDiscoveryContent.do";
	/** 科普发现中的列表详情 */
	public static final String URL_DISCOVER_LIST = URL_HOST + "fang/getSciDiscovery.do";
	/** 发现首页上的轮播图 */
	public static final String URL_CAROUSEL_IMAGE = URL_HOST + "fang/getSciDiscoverycirCulationImage.do";
}
