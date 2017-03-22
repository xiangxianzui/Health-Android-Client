package edu.monash.infotech.health;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;


import java.util.ArrayList;
import java.util.List;


public class BaiduMapActivity extends Activity {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();
    //private MyLocationConfiguration.LocationMode mCurrentMode;
    //BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    //POI Search相关
    private PoiSearch mPoiSearch;
    public MyPOIListener myPOIListener = new MyPOIListener();


    private TextureMapView mMapView;
    // 百度地图对象
    private BaiduMap mBaiduMap;

    private MyLocationData locData;

    // UI相关
    boolean isFirstLoc = true; // 是否首次定位

    BitmapDescriptor bitmaps[];
    Marker markers[];

    //GeoCoder
    private GeoCoder mGeoCoder = null;
    public MyGeoListener myGeoListener = new MyGeoListener();

    //Route Plan
    private RoutePlanSearch mRoutePlan = null;
    public MyRoutePlanListener myRoutePlanListener = new MyRoutePlanListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);

        //load marker icons
        bitmaps = new BitmapDescriptor[10];
        bitmaps[0] = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        bitmaps[1] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
        bitmaps[2] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
        bitmaps[3] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markd);
        bitmaps[4] = BitmapDescriptorFactory.fromResource(R.drawable.icon_marke);
        bitmaps[5] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markf);
        bitmaps[6] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markg);
        bitmaps[7] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markh);
        bitmaps[8] = BitmapDescriptorFactory.fromResource(R.drawable.icon_marki);
        bitmaps[9] = BitmapDescriptorFactory.fromResource(R.drawable.icon_markj);

        //initialize markers
        markers = new Marker[10];

        //mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//        View.OnClickListener btnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("hiuhfwehfiwehifuhwe");
//                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
//            }
//        };
        //requestLocButton.setOnClickListener(btnClickListener);

        // 地图初始化
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(10000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        //POI Search
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(myPOIListener);

        //GeoCoder
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(myGeoListener);

        //Route Plan
        mRoutePlan = RoutePlanSearch.newInstance();
        mRoutePlan.setOnGetRoutePlanResultListener(myRoutePlanListener);

        //OnClick listener for marker
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(marker.getPosition()));
                PlanNode stNode = PlanNode.withLocation(new LatLng(locData.latitude, locData.longitude));
                PlanNode enNode = PlanNode.withLocation(marker.getPosition());
                mRoutePlan.walkingSearch((new WalkingRoutePlanOption())
                        .from(stNode)
                        .to(enNode)
                );
                return false;
            }
        });

    }

    /**
     * Route Plan监听函数
     */
    public class MyRoutePlanListener implements OnGetRoutePlanResultListener{
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            //获取步行线路规划结果
            if(result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getApplicationContext(), "No route found!", Toast.LENGTH_SHORT).show();
            }
            if(result.error == SearchResult.ERRORNO.NO_ERROR){
                WalkingRouteLine thisRouteLine = result.getRouteLines().get(0);
                AlertDialog.Builder builder = new AlertDialog.Builder(BaiduMapActivity.this);
                builder.setTitle("Route Information"); //设置标题
                String routePlanInfoStr = "Distance: " + thisRouteLine.getDistance() + "m\n"
                        + "Average time: " + thisRouteLine.getDuration()/60 + "min\n";
                builder.setMessage(routePlanInfoStr); //设置内容
                builder.setIcon(R.drawable.log);//设置图标，图片id即可
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //参数都设置完成了，创建并显示出来
                builder.create().show();

                //draw route line
                List<OverlayOptions> overlayList = new ArrayList<OverlayOptions>();
                if(thisRouteLine.getAllStep()!=null && thisRouteLine.getAllStep().size()>0){
                    for(WalkingRouteLine.WalkingStep step : thisRouteLine.getAllStep()){
                        Bundle b = new Bundle();
                        b.putInt("index", thisRouteLine.getAllStep().indexOf(step));
                        if(step.getEntrance() != null){
                            overlayList.add((new MarkerOptions())
                                .position(step.getEntrance().getLocation())
                                    .rotate((360 - step.getDirection()))
                                    .zIndex(10)
                                    .anchor(0.5f, 0.5f)
                                    .extraInfo(b)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dot)));
                        }
                        if(thisRouteLine.getAllStep().indexOf(step)==(thisRouteLine.getAllStep().size()-1)
                                 && step.getExit()!=null){
                            overlayList.add((new MarkerOptions())
                                .position(step.getExit().getLocation())
                                    .anchor(0.5f, 0.5f)
                                    .zIndex(10)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dot)));
                        }
                    }
                }
                if(thisRouteLine.getAllStep()!=null && thisRouteLine.getAllStep().size()>0){
                    LatLng lastStepLastPoint = null;
                    for(WalkingRouteLine.WalkingStep step : thisRouteLine.getAllStep()){
                        List<LatLng> watPoints = step.getWayPoints();
                        if(watPoints != null){
                            List<LatLng> points = new ArrayList<LatLng>();
                            if(lastStepLastPoint != null){
                                points.add(lastStepLastPoint);
                            }
                            points.addAll(watPoints);
                            overlayList.add(new PolylineOptions().points(points).width(10)
                            .color(Color.argb(178, 0, 78, 255)).zIndex(0));
                            lastStepLastPoint = watPoints.get(watPoints.size()-1);
                        }
                    }
                }
                mBaiduMap.addOverlays(overlayList);
            }
        }
        public void onGetTransitRouteResult(TransitRouteResult result) {
            //获取公交换乘路径规划结果
        }
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            //获取驾车线路规划结果
        }
        public void onGetBikingRouteResult(BikingRouteResult result) {
            //获取驾车线路规划结果
        }
    }


    /**
     * GeoCoder监听函数
     */
    public class MyGeoListener implements OnGetGeoCoderResultListener{
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result){
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getApplicationContext(), "No information found!", Toast.LENGTH_SHORT).show();
                return;
            }
            String strInfo = String.format("Latitude：%f Longtitude：%f",
                    result.getLocation().latitude, result.getLocation().longitude);
            Toast.makeText(getApplicationContext(), strInfo, Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result){
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getApplicationContext(), "No information found!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), result.getAddress(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * POI监听函数
     */
    public class MyPOIListener implements OnGetPoiSearchResultListener{
        public void onGetPoiResult(PoiResult result){
            //获取POI检索结果
            if(result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
                Toast.makeText(getApplicationContext(), "No parks found within 5KM!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(result.error == SearchResult.ERRORNO.NO_ERROR){
                List<PoiInfo> parkList = result.getAllPoi();
                int numOfPark = parkList.size();
                for(int i=0; i<numOfPark; i++){
                    double latitude = parkList.get(i).location.latitude;
                    double longtitude = parkList.get(i).location.longitude;
                    //System.out.println("(" + latitude + ", " + longtitude + ")");
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(latitude, longtitude))//设置marker的位置
                            .icon(bitmaps[i])//设置marker图标
                            .draggable(true);//设置手势拖拽
                    //在地图上添加Marker，并显示
                    markers[i] = (Marker)(mBaiduMap.addOverlay(markerOptions));

                }
                return;

            }
            System.out.println("Nearby: "+result.getAllPoi().size());
        }
        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }
    }



    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            System.out.println("Getting Location...");
            if (location == null || mMapView == null) {
                return;
            }
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(14.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.location);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions markerOptions = new MarkerOptions()
                        .position(ll)//设置marker的位置
                        .icon(bitmap)//设置marker图标
                        .draggable(true);//设置手势拖拽
                //在地图上添加Marker，并显示
                Marker marker = (Marker)(mBaiduMap.addOverlay(markerOptions));

                //
                OverlayOptions circleOptions = new CircleOptions()
                    .fillColor(0x000000FF)
                    .center(ll)
                    .stroke(new Stroke(5, 0xAA000000))
                    .radius(5000);//半径5公里
                mBaiduMap.addOverlay(circleOptions);

                mPoiSearch.searchNearby((new PoiNearbySearchOption())
                                .keyword("公园")
                                .sortType(PoiSortType.distance_from_near_to_far)
                                .location(ll)
                                .pageCapacity(10)
                                .radius(5000)
                );

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }
}



