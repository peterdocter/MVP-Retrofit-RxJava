package com.byl.mvpdemo.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.byl.mvpdemo.model.modelbean.NewsBean;
import com.byl.mvpdemo.model.mvpview.NewsMvpView;
import com.byl.mvpdemo.presenter.base.BasePresenter;
import com.byl.mvpdemo.util.LogUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by baiyuliang on 2016-7-14.
 */
public class ImagesPresenter extends BasePresenter<NewsMvpView> {

    Subscription mSubscription;

    public ImagesPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(NewsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getImages(String num, String page, String rand) {
        checkViewAttached();
        mSubscription = getApiService().girls(num, page, rand)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<NewsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().fail("获取图片失败");
                        LogUtil.e("获取图片失败>>" + e.getMessage());
                    }

                    @Override
                    public void onNext(NewsBean newsBean) {
                        if (!newsBean.getShowapi_res_code().equals("0")
                                || newsBean.getShowapi_res_body() == null
                                || TextUtils.isEmpty(newsBean.getShowapi_res_body().getCode())
                                || !newsBean.getShowapi_res_body().getCode().equals("200")) {
                            getMvpView().fail("获取图片失败");
                        } else {
                            if (page.equals("1")) {//刷新
                                if (newsBean.getShowapi_res_body().getNewslist() == null
                                        || newsBean.getShowapi_res_body().getNewslist().size() <= 0) {
                                    getMvpView().fail("当前无图片");
                                } else {
                                    getMvpView().refreshSuccess(newsBean);
                                }
                            } else {//加载更多
                                if (newsBean.getShowapi_res_body().getNewslist() == null
                                        || newsBean.getShowapi_res_body().getNewslist().size() <= 0) {
                                    getMvpView().fail("没有更多数据啦");
                                } else {
                                    getMvpView().loadMoreSuccess(newsBean);
                                }
                            }

                        }
                    }
                });
    }

}
