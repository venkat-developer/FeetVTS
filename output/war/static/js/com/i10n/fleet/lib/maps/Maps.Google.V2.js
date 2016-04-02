(function() {
	var $YU = YAHOO.util;
	var $D = YAHOO.util.Dom;
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $YW = YAHOO.widget;
	var $W = getPackageForName("com.i10n.fleet.widget.lib");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var oGMap;
	var isKMLRequestSent=false;
	/**
	 * An Adapter Widget for Other Widgets to interact with Google Map V2 API.
	 * @param {Object} el
	 * @param {Object} oArgs
	 * @author sabarish
	 */
	$W.Maps = function(el, oArgs) {
		this.initFleetMap(el, oArgs);
		YAHOO.util.Connect.asyncRequest('GET',  "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=alert&dataView=assignment&isGoogleHit=true",this.oCallback,null);
	};
	/**
	 * An Adapter Map to interact with GMap2
	 * @param {Object} el
	 * @param {Object} oArgs
	 */
	$W.Maps.Map = function(el, oArgs) {
		$W.Maps.Map.superclass.constructor.call(this, el);
		this.enableScrollWheelZoom();
		this.enableContinuousZoom();
		this.setCenter(oArgs.center);
		this.setZoom(oArgs.zoom);
		/**
		 * Adding The Fleet Custom ZoomBar
		 * SlideRatio is kept as null
		 */
		this.addControl(new $W.Maps.FleetZoomBar(this));

		/**
		 * code for Adding custom i10n  map.
		 * 
		 */
		var copyright = new GCopyright(1, new GLatLngBounds(new GLatLng(-90,
				-180), new GLatLng(90, 180)), 0, "? 2014 Harman International");
		var copyrightCollection = new GCopyrightCollection('i10n');
		copyrightCollection.addCopyright(copyright);

		var tilelayers = [ new GTileLayer(copyrightCollection, 0, 17) ];
		tilelayers[0].getTileUrl = $W.Maps.CustomGetTileUrl;
		var custommap = new GMapType(tilelayers, new GMercatorProjection(18),
				"i10n", {
			errorMessage : "No data available"
		});
		/**
		 * code for Adding custom  yahoo map  
		 * 
		 */
		var yCopyright = new GCopyright(1, new GLatLngBounds(new GLatLng(-90,
				-180), new GLatLng(90, 180)), 0, "Yahoo Map");
		var yCopyrightCollection = new GCopyrightCollection('Yahoo');
		yCopyrightCollection.addCopyright(yCopyright);

		var yTilelayers = [ new GTileLayer(yCopyrightCollection, 0, 17) ];
		yTilelayers[0].getTileUrl = $W.Maps.yCustomGetTileUrl;

		var yCustommap = new GMapType(yTilelayers, new GMercatorProjection(18),
				"yahoo", {
			errorMessage : "No data available"
		});
		this.addMapType(custommap);
		this.addMapType(yCustommap);
		this.addControl(new GMapTypeControl());
		this.addMapType(G_PHYSICAL_MAP);
		this.checkResize();
		if (_instances && _instances.view && _instances.view.onPageResize) {
			_instances.view.onPageResize.subscribe(function(oArgs) {
				this.checkResize();
			}, this, true);
		}
	};
	$L.extend($W.Maps.Map, GMap2, {
		/**
		 * fits the current map to the bounds given
		 * @param {Object} oBounds
		 */
		fitBounds : function(oBounds) {
		var nZoom = this.getBoundsZoomLevel(oBounds);
		this.setCenter(oBounds.getCenter());
		this.setZoom(nZoom);
	}
	});
	/**
	 * Custom Icon representation for Markers to support sprited images.
	 * @param {Object} oArgs
	 */
	$W.Maps.Icon = function(oArgs) {
		this._oArgs = oArgs;
	};
	$L.augmentObject($W.Maps.Icon.prototype, {
		clone : function() {
		return new $W.Maps.Icon(this._oArgs);
	},
	/**
	 * Creates a div that can hold image as background.
	 */
	_createElement : function() {
		var elDiv = document.createElement('div');
		$D.addClass(elDiv, "map-marker-icon");
		$D.setStyle(elDiv, "position", "absolute");
		$D.setStyle(elDiv, "cursor", "pointer");
		$D.setStyle(elDiv, "width", this._oArgs.size.width + "px");
		$D.setStyle(elDiv, "height", this._oArgs.size.height + "px");
		$D.setStyle(elDiv, "background", "no-repeat url('"
				+ this._oArgs.url + "') " + (0 - this._oArgs.sprite.x)
				+ "px " + (0 - this._oArgs.sprite.y) + "px");
		return elDiv;
	},
	/**
	 * Draws the icon on a div and return the div.
	 */
	draw : function() {
		return this._createElement();
	},
	getAnchor : function() {
		return this._oArgs.anchor;
	}
	});
	$L.augmentObject($W.Maps, {
		"ATTR_MAP" : "map",
		//"DEFAULT_LATLNG" : new GLatLng(23.259849,77.412672),
		"DEFAULT_LATLNG" : new GLatLng(12.971599, 77.594563),
		"MARKER_SPRITE_CONFIG": {
		"vehicles": {
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/idle.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(20, 25)
		}),
		"idlemapreport": new $W.Maps.Icon({
			url: '/static/img/maps/pin-orange.png',
			size: new GSize(20, 25),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 25)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/blurb.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/0360.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/45.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/90.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/135.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/180.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/225.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/270.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/315.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),                
		"onlinestart": new $W.Maps.Icon({
			url: '/static/img/maps/From.png',
			size: new GSize(45, 45),
			sprite: new GPoint(2, 2),
			anchor: new GPoint(23, 25)
		}),
		"onlineend": new $W.Maps.Icon({
			url: '/static/img/maps/To.png',
			size: new GSize(45, 45),
			sprite: new GPoint(2, 2),
			anchor: new GPoint(23, 25)
		}),
		"trackpoint":new $W.Maps.Icon({
			url: '/static/img/buttons/bullet.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/offline.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(10, 35)

		})
	},

	"trackpoint" : {
		"trackpoint":new $W.Maps.Icon({
			url: '/static/img/buttons/bullet.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint0":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint0.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint45":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint45.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint90":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint90.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint135":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint135.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint180":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint180.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint225":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint225.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint270":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint270.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		}),
		"trackpoint315":new $W.Maps.Icon({
			url: '/static/img/trackpoint/trackpoint315.png',
			size: new GSize(7, 7),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(3, 5)
		})
	},

	"custom-1":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/idle.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/bike.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/0360.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45),
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/45.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/90.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/135.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/180.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/225.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/270.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/315.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),                
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/offline.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/bike/stopped.png',
			size: new GSize(39, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(12, 45)
		})           	

	},

	"custom-2":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/idle.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/blurb.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/0360.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/45.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/90.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/135.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/180.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/225.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/270.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/315.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/offline.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		})

	},

	"custom-3":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/blue.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busblue/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})

	},

	"custom-4":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/brown.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busbrown/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-5":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/darkyellow.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busdarkyellow/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-6":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/green.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busgreen/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-7":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/pink.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspink/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-8":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/purple.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/buspurple/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-9":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/skyblue.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busskyblue/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}) 
	},

	"custom-10":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/violet.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busviolet/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-11":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(25, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/yellow.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/0360.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/45.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/90.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/135.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/180.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/225.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/270.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/busyellow/315.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/busoffline.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/busstopped.png',
			size: new GSize(40, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-12":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/idle.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/car.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/0360.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/45.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/90.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/135.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/180.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/225.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/270.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/315.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/offline.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/car/stopped.png',
			size: new GSize(30, 20),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-13":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/idle.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/jeep.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/0360.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/45.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/90.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/135.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/180.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/225.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/270.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/315.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/offline.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep/stopped.png',
			size: new GSize(36, 42),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-14":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/idle.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/jeep1.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/0360.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/45.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/90.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/135.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/180.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/225.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/270.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/315.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/offline.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/jeep1/stopped.png',
			size: new GSize(38, 29),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-15":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/idle.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/pointer.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/0360.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/45.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/90.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/135.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/180.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/225.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/270.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/315.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/offline.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/pointer/stopped.png',
			size: new GSize(15, 44),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-16":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/idle.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/tractor.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/0360.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/45.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/90.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/135.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/180.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/225.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/270.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/315.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/offline.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/tractor/stopped.png',
			size: new GSize(40, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-17":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/idle.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/truck.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/0360.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/45.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/90.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/135.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/180.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/225.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/270.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/315.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/offline.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/truck/stopped.png',
			size: new GSize(40, 30),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-18":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/idle.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/van.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/0360.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/45.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/90.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/135.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/180.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/225.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/270.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/315.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/offline.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/van/stopped.png',
			size: new GSize(44, 36),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},

	"custom-19":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/idle.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/fueltanker.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/0360.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/45.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/90.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/135.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/180.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/225.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/270.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/315.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/offline.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/fueltanker/stopped.png',
			size: new GSize(50, 40),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},
	"custom-20":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)*/
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/20.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},
	"custom-21":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/idle.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)*/
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/21.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/0360.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/45.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/90.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/135.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/180.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/225.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/270.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/315.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/offline.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/stopped.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},
	"custom-22":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)*/
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/22.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},
	"custom-25":{
		"idle": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)*/
		}),
		"idleblurb": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(20, 35),
			sprite: new GPoint(2, 0),
			anchor: new GPoint(32, 40)
		}),
		"online": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online0": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online45": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online90": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online135": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online180": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online225": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online270": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"online315": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"offline": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		}),
		"stopped": new $W.Maps.Icon({
			url: '/static/img/vehicles/tncsc/25.png',
			size: new GSize(60, 66),
			sprite: new GPoint(0, 0),
			anchor: new GPoint(14, 22)
		})
	},
	}
	});
	$L.extend($W.Maps, $YU.AttributeProvider);
	$L.augmentObject($W.Maps.prototype, {
		/**
		 * Initializes the Attributes
		 */
		initAttributes : function() {
		/**
		 * @attribute map
		 * @description Current Map represented by the object.
		 * @type Object
		 */
		this.setAttributeConfig($W.Maps.ATTR_MAP, {
			value : null
		});
	},
	/**
	 * initialize maps
	 * @param {Object} el
	 * @param {Object} oArgs
	 */
	initFleetMap : function(el, oArgs) {
		this.initAttributes();
		var oCenterLatLng = $W.Maps.DEFAULT_LATLNG;
		var oConfig = {
				zoom : 8,
				center : oCenterLatLng
		};
		oGMap = new $W.Maps.Map(el, oConfig);
		this.set($W.Maps.ATTR_MAP, oGMap);
		oGMap.checkResize();
	},
	/**
	 * Adapter to check for resize;
	 */
	checkResize : function() {
		var oGMap = this.get($W.Maps.ATTR_MAP);
		if (oGMap) {
			oGMap.checkResize();
		}
	}
	});
	/**
	 * Custom Marker Tabbed Info Window for GoogleMaps. Uses Custom Styling and is configurable.
	 * @param {Object} oArgs
	 */
	$W.Maps.FleetInfoWindow = function(oArgs) {
		/**
		 * For kp and k1 clients need to enable this request.
		 */
		/*if(!isKMLRequestSent){
			YAHOO.util.Connect.asyncRequest('GET',  "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
					"&data=view&subpage=liveData&dataView=assignment&kmlFolderSize=true",this.getKMLFiles,null);	
			isKMLRequestSent=true;
		}*/
		
		/**
		 * For Bhopal client need to enable this block.
		 */
		/*oArgs.map.addOverlay(this);
		var bhopal = new GGeoXml("http://bpl.gwtrack.com/static/kml/bhopal.kml");
		oArgs.map.addOverlay(bhopal);*/
		/**
		 * For TNCSC client need to enable this block.
		 */

	/*	var tncsc = new GGeoXml("http://tpds.gwtrack.com/static/kml/tncsc.kml");
		var tncscgodown = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/godown.kml");
		var Gummidipondi = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Gummidipondi.kml");
		var Pallipat = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Pallipat.kml");
		var Ponneri = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Ponneri.kml");
		var Poonamallee = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Poonamallee.kml");
		var Thiruthani = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Thiruthani.kml");
		var Hosur = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Hosur.kml");
		var Denkanikottai = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Denkanikottai.kml");
		var Thiruvallur = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Thiruvallur.kml");
		var Uthukottai = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Uthukottai.kml");
		var Pochampalli = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Pochampalli.kml");
		var Krishnagiri = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Krishnagiri.kml");
		var Uthangarai = new GGeoXml("http://tpds.gwtrack.com/static/kml/TNCSC/Uthangarai.kml");

		oArgs.map.addOverlay(tncsc);
		oArgs.map.addOverlay(tncscgodown);
		oArgs.map.addOverlay(Gummidipondi);
		oArgs.map.addOverlay(Pallipat);
		oArgs.map.addOverlay(Ponneri);
		oArgs.map.addOverlay(Poonamallee);
		oArgs.map.addOverlay(Thiruthani);
		oArgs.map.addOverlay(Hosur);
		oArgs.map.addOverlay(Denkanikottai);
		oArgs.map.addOverlay(Thiruvallur);
		oArgs.map.addOverlay(Uthukottai);
		oArgs.map.addOverlay(Pochampalli);
		oArgs.map.addOverlay(Krishnagiri);
		oArgs.map.addOverlay(Uthangarai);*/
	};
	$W.Maps.addKMLLayers = function(oArgs) {
		var fileName="/kml/"+oArgs.fileName;
		var kmlLayer = new GGeoXml(fileName);
		oGMap.addOverlay(kmlLayer);
	};
	$L.extend($W.Maps.FleetInfoWindow, GOverlay, {
		/**
		 * Map where this info window is displayed
		 */
		_oMap : null,
		/**
		 * Current position in LonLat where this InfoWindow is displayed.
		 */
		_oPosition : null,
		/**
		 * Pixels to offset the info window while displaying it.
		 */
		OFFSET : new GPoint(0, -322),
		/**
		 * Top Right Offset to calculate bounds while panning.
		 */
		PAN_TR_OFFSET : new GPoint(300, -330),
		/**
		 * Bottom Left Offset to calculate bounds while panning.
		 */
		PAN_BL_OFFSET : new GPoint(-5, -5),
		/**
		 * initialization function
		 * @param {Object} oGMap
		 */
		initialize : function(oGMap) {
		var elBase = this._createElement();
		oGMap.getPane(G_MAP_FLOAT_PANE).appendChild(elBase);
		this._oMap = oGMap;
		this.elBase = elBase;
		this._oTabView = new $YW.TabView(this.elBase);

		$E.addListener($D.getElementsByClassName("close-button", null,
				this.elBase), "click", function(oArgs, oArgsSelf) {
			this.display(false);
		}, this, true);

	},
	getKMLFiles:{
		success: function(o) {
		var oResponse=JSON.parse(o.responseText);
		var aData1=$U.Arrays.mapToArray(oResponse);
		var veh;
		var isdata;
		var allmarkers = {};
		var that=this;
		for(var m=0 ; m< aData1.length;m++){
			var mobject=aData1[m];
			var aData=$U.Arrays.mapToArray(mobject);
			for(var n=0 ; n< aData.length;n++){
				var nobject=aData[n];
				var ndat=$U.Arrays.mapToArray(nobject);
				for(var j=0;j<ndat.length;j++){
					var jobject=ndat[j];
					var obj=$U.Arrays.mapToArray(jobject);
					for(var k=0;k<obj.length;k++){
						$W.Maps.addKMLLayers(obj[k]);
					}
				}
			}
		}
	},
	failure: function(o) {

	},
	},
	/**
	 * Creates the dom element base in the template given.
	 */
	_createElement : function() {
		var elTemplateContainer = $D.get("template-info-window");
		var elBase = ($D.getElementsByClassName("marker-info-window", null,
				elTemplateContainer)[0]).cloneNode(true);
		elBase.style.display = 'none';
		return elBase;
	},
	/**
	 * purges the info window.
	 */
	remove : function() {
		this._oTabView.destroy();
		$E.purgeElement(this.elBase, true);
		this.elBase.parentNode.removeChild(this.elBase);
		this.elBase = null;
		this._oMap = null;
		this._oPosition = null;
	},
	/**
	 * redraws the info window
	 */
	redraw : function(bForce) {
		if (bForce && this._oMap && this._oPosition) {
			var oPixel = this._oMap.fromLatLngToDivPixel(this._oPosition);
			this.draw(oPixel);
		}
	},
	/**
	 * API used by all the users to move this info window to a specific position
	 * @param {Object} oArgs
	 */
	drawAndMoveTo : function(oArgs) {
		this.setContent(oArgs);
		this._oPosition = oArgs.position;
		var oPixel = this._oMap.fromLatLngToDivPixel(this._oPosition);
		this.draw(oPixel);
		this.panMap();
		// Zoom to the selected entity
		this._oMap.setZoom(18);
		this._oMap.setCenter(oArgs.position);
		this.display(true);		
	},
	/**
	 * draws the info window
	 * @param {Object} oPixel
	 */
	draw : function(oPixel) {
		this.moveTo(oPixel);
	},
	/**
	 * sets the display property of the info window
	 * @param {Boolean} bDisplay
	 */
	display : function(bDisplay) {

		var sDisplay = bDisplay ? "block" : "none";
		$D.setStyle(this.elBase, "display", sDisplay);
	},
	/**
	 * Pans the map to display the info window optimally.
	 */
	panMap : function() {
		var oBounds = this.calculateBounds();
		var oCurrentBounds = this._oMap.getBounds();
		if (!oCurrentBounds.containsBounds(oBounds)) {
			this._oMap.panTo(oBounds.getCenter());
		}
	},
	/**
	 * Calculate Bounds to display the info windows
	 */
	calculateBounds : function() {
		var oPosition = this._oPosition;
		var oPosPixel = this._oMap.fromLatLngToDivPixel(oPosition);
		var oBottomLeft = new GPoint(oPosPixel.x + this.PAN_BL_OFFSET.x,
				oPosPixel.y + this.PAN_BL_OFFSET.y);
		var oTopRight = new GPoint(oPosPixel.x + this.PAN_TR_OFFSET.x,
				oPosPixel.y + this.PAN_TR_OFFSET.y);
		var oBounds = new $W.Maps.LatLngBounds();
		oBounds.extend(this._oMap.fromDivPixelToLatLng(oBottomLeft));
		oBounds.extend(this._oMap.fromDivPixelToLatLng(oTopRight));
		return oBounds;
	},
	/**
	 * Sets the contents of the tabs based on the arguments passed
	 * @param {Object} oArgs
	 */
	setContent : function(oArgs) {
		if (oArgs && oArgs.content) {
			for ( var sTabId in oArgs.content) {
				var elTabContainer = $D.getElementsByClassName(
						"tabcontent-" + sTabId, null, this.elBase)[0];
				if (elTabContainer) {
					elTabContainer.innerHTML = oArgs.content[sTabId];
				}
			}
		}
	},
	/**
	 * Moves the info window to the required position
	 * @param {Object} px
	 */
	moveTo : function(oPixel) {
		if (oPixel) {
			this._oPixel = oPixel;
		}
		if (this.elBase) {
			if (this._oPixel === null) {
				this.display(false);
			} else {
				$D.setStyle(this.elBase, "top",
						(this._oPixel.y + this.OFFSET.y) + "px");
				$D.setStyle(this.elBase, "left",
						(this._oPixel.x + this.OFFSET.x) + "px");
			}
		}
	}
	});
	/**
	 * Custom Marker Adapter for Google Maps V2 API.
	 * @param {Object} oConfig
	 */
	$W.Maps.Marker = function(oConfig) {
		var oOptions = oConfig.options;
		oConfig.icon = $W.Maps.MARKER_SPRITE_CONFIG[oOptions.iconType][oOptions.iconSubType];

		this._oMap = oConfig.map;
		this._oIcon = oConfig.icon;
		this._oPosition = oConfig.position;
		this._oMap.addOverlay(this);
	};
	$L.extend($W.Maps.Marker, GOverlay,
			{
		/**
		 * initializes the map.
		 * @param {Object} oGMap
		 */
		initialize : function(oGMap) {
		var elBase = this.createIcon();
		this.elBase = elBase;
		this.display(false);
		oGMap.getPane(G_MAP_FLOAT_PANE).appendChild(elBase);
		this.redraw(true);
		this.display(true);
		this._addListeners();
	},
	/**
	 * Adds listeneres to the base elment of the marker.
	 */
	_addListeners : function() {
		$E.addListener(this.elBase, "click", function(oArgs) {
			GEvent.trigger(this, "click");
		}, this, true);
	},
	/**
	 * Returns the position of the marker.
	 */
	getPosition : function() {
		return this._oPosition;
	},
	/**
	 * Clears this marker from the map.
	 */
	clear : function() {
		this._oMap.removeOverlay(this);
		$E.purgeElement(this.elBase);
		this._oMap = null;
		this._oPosition = null;
		this._oIcon = null;
		this._oPixel = null;
	},
	remove : function() {
		this.elBase.parentNode.removeChild(this.elBase);
	},
	/**
	 * sets the display property of the info window
	 * @param {Boolean} bDisplay
	 */
	display : function(bDisplay) {
		var sDisplay = bDisplay ? "block" : "none";
		$D.setStyle(this.elBase, "display", sDisplay);
	},
	/**
	 * draws the icon for the marker
	 */
	createIcon : function() {
		return this._oIcon.draw();
	},
	/**
	 * Redraws the marker when there is a change in the map which requires a redraw.
	 * @param {Object} bForce
	 */
	redraw : function(bForce) {
		if (bForce && this._oMap && this._oPosition) {
			var oPixel = this._oMap
					.fromLatLngToDivPixel(this._oPosition);
			this.draw(oPixel);
		}
	},
	/**
	 * Draws the marker at the specified pixel position.
	 * @param {Object} oPixel
	 */
	draw : function(oPixel) {
		if (oPixel) {
			this._oPixel = oPixel;
		}
		if (this.elBase) {
			if (this._oPixel === null) {
				this.display(false);
			} else {
				$D
				.setStyle(this.elBase, "top",
						(this._oPixel.y - this._oIcon
								.getAnchor().y)
								+ "px");
				$D
				.setStyle(this.elBase, "left",
						(this._oPixel.x - this._oIcon
								.getAnchor().x)
								+ "px");
			}
		}
	}
			});
	/**
	 * Fleet Custom Class representing latitude and longitude.
	 * @extends $GM.LatLng
	 * @param {Object} oArgs
	 */
	$W.Maps.LatLng = function(oArgs) {
		$W.Maps.LatLng.superclass.constructor.call(this, oArgs.lat, oArgs.lon);
	};
	$L.extend($W.Maps.LatLng, GLatLng);
	/**
	 * Fleet Custom Class representing latitude and longitude bounds.
	 * @extends $GLatLng
	 * @param {Object} oArgs
	 */
	$W.Maps.LatLngBounds = function(oArgs) {
		if (!oArgs) {
			oArgs = {};
		}
		$W.Maps.LatLngBounds.superclass.constructor.call(this, oArgs.sw,
				oArgs.ne);
	};
	$L.extend($W.Maps.LatLngBounds, GLatLngBounds, {
		extend : function(oBoundable) {
		if (oBoundable instanceof GLatLngBounds) {
			$W.Maps.LatLngBounds.superclass.extend.call(this, oBoundable
					.getSouthWest());
			$W.Maps.LatLngBounds.superclass.extend.call(this, oBoundable
					.getNorthEast());
		} else {
			$W.Maps.LatLngBounds.superclass.extend.call(this, oBoundable);
		}
	}
	});
	$W.Maps.Event = {/**
	 * Is A Static Class;
	 */
	};
	$L.augmentObject($W.Maps.Event, GEvent);
	$W.Maps.PolyLine = function(oArgs) {
		if (!oArgs) {
			oArgs = {};
		}
		var sColor = (oArgs.color) ? oArgs.color : "#0000FF";
		var nWeight = (oArgs.weight) ? oArgs.weight : 5;
		var nOpacity = (oArgs.opacity) ? oArgs.opacity : 0.5;
		var oPolyline = new GPolyline(oArgs.positions, sColor, nWeight,
				nOpacity);
		if (oArgs.map) {
			this._oMap = oArgs.map;
			this._oMap.addOverlay(oPolyline);
		}
		this._oPolyline = oPolyline;
	};
	$L.augmentObject($W.Maps.PolyLine.prototype, {
		clear : function() {
		this._oMap.removeOverlay(this._oPolyline);
		this._oMap = null;
		this._oPolyline = null;
	},
	getBounds : function() {
		return this._oPolyline.getBounds();
	}
	});
	/**
	 * Function for i10n Zoom levels and url implementation.
	 */
	$W.Maps.CustomGetTileUrl = function(a, b) {

		var z = 17 - b;

		var f = "/maps/"+a.x+"-"+a.y+"-"+(z)+ ".png";		
		return f;

	};
	/**
	 * Function for yahoo Zoom levels and url implementation  
	 */
	$W.Maps.yCustomGetTileUrl = function(a, b) {

		var newY = ((1 << b) >> 1) - 1 - a.y;
		b = b + 1;
		if (b == 1)
			b = 2;
		var f = "http://us.maps1.yimg.com/us.tile.maps.yimg.com/tl?v=4.2&x="
				+ a.x + "&y=" + newY + "&z=" + b + "&r=1";
		//console.log(f);
		return f;
	};
	/**
	 * A Custom Fleet ZoomBar for Google Maps
	 * @extends Gcontrol
	 * @param {Object} map
	 * @param {Object} slideRatio
	 */
	$W.Maps.FleetZoomBar = function(map, slideRatio) {
		this.oMap = map;
		if (slideRatio) {
			this.slideRatio = slideRatio;
		}
		this.slideFactor = 50;
	};
	$L.augmentObject($W.Maps.FleetZoomBar, {
		PAN_SOUTH : "pandown",
		PAN_NORTH : "panup",
		PAN_WEST : "panleft",
		PAN_EAST : "panright",
		ZOOM_OUT : "zoomout",
		ZOOM_IN : "zoomin",
		ZOOM_BAR : "zoombar",
		SLIDER : "slider",
		PAN_TO_CENTER : "panToCenter"
	});

	$L.augmentObject(
			$W.Maps.FleetZoomBar.prototype,
			{
				getSlideFactor : function(dim) {
				if (this.slideRatio) {
					if (dim == "width") {
						return this.oMap.getSize().width
								* this.slideRatio;
					} else {
						return this.oMap.getSize().height
								* this.slideRatio;
					}
				} else {
					return this.slideFactor;
				}
			},
			/**
			 * Sets the Slider Position according to new ZoomLevel
			 * @param {Object} option
			 */
			moveSlider : function(option) {
				var baseEl = this.slider;
				var top;
				if (option) {
					top = parseInt($D.getStyle(baseEl, 'top'), 10) - 11;
					if (top >= this.slideMinTop) {
						$D.setStyle(baseEl, 'top', top + "px");
					}
				} else {
					top = parseInt($D.getStyle(baseEl, 'top'), 10) + 11;
					if (top < this.slideMaxTop - 5) {
						$D.setStyle(baseEl, 'top', top + "px");
					}
				}
			},
			/**
			 * Adds a Button and action according to id
			 * @param {Object} id
			 */
			addButton : function(id) {
				var el = document.createElement("div");
				$D.addClass(el, id);
				var parent = this.container;
				var map = this.oMap;
				var zoombar = this;
				parent.appendChild(el);
				switch (id) {
				case $W.Maps.FleetZoomBar.ZOOM_IN:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						zoombar.moveSlider("zoomin");
						if (map.getZoom() < 18) {
							map.zoomIn();

						}
					});
					break;
				case $W.Maps.FleetZoomBar.ZOOM_OUT:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						zoombar.moveSlider();
						if (map.getZoom() > 0) {
							map.zoomOut();
						}
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_EAST:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("width");
						map.panBy(new GSize(distance, 0));
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_WEST:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("width");
						map.panBy(new GSize(-distance, 0));
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_NORTH:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("height");
						map.panBy(new GSize(0, distance));
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_SOUTH:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("height");
						map.panBy(new GSize(0, -distance));
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_TO_CENTER:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						map.panTo($W.Maps.DEFAULT_LATLNG);
					});
					break;
				case $W.Maps.FleetZoomBar.SLIDER:
					this.slider = el;
					var posy;

					$W.Maps.Event
					.addDomListener(
							el,
							"mousedown",
							function(event) {
								if (event.pageY) {
									posy = event.pageY;
								} else if (event.clientY) {
									posy = event.clientY
											+ document.body.scrollTop
											+ document.documentElement.scrollTop;
								}
								this.startDrag = posy;
							});
					$W.Maps.Event.addDomListener(el, "mouseup",
							function(event) {
						this.startDrag = null;
						var diff = parseInt($D.getStyle(el,
								'top'), 10)
								- parseInt($D.getStyle(
										zoombar._zoombar,
										'top'), 10);
						var level = Math.abs(Math
								.floor((diff) / 11));
						map.setZoom(18 - level - 1);
						var newtop = (level) * 11
								+ zoombar.slideMinTop;
						$D.setStyle(el, 'top',
								newtop + 'px');
					});

					$W.Maps.Event
					.addDomListener(
							el,
							"mousemove",
							function(event) {
								if (this.startDrag !== null
										&& typeof this.startDrag !== 'undefined') {
									if (event.pageY) {
										posy = event.pageY;
									} else if (event.clientY) {
										posy = event.clientY
												+ document.body.scrollTop
												+ document.documentElement.scrollTop;
									}
									var top = parseInt($D
											.getStyle(el,
													'top'),
													10)
													- (this.startDrag - posy);
									if ((event.clientY - zoombar
											.pagePosition(zoombar._zoombar)[1]) > 0
											&& (event.clientY - zoombar
													.pagePosition(zoombar._zoombar)[1]) < parseInt(
															$D
															.getStyle(
																	zoombar._zoombar,
																	'height'),
																	10) - 5) {
										$D.setStyle(el,
												'top',
												top + "px");
										this.startDrag = posy;
									}
								}
							});

					break;
				case $W.Maps.FleetZoomBar.ZOOM_BAR:
					this._zoombar = el;
					$W.Maps.Event
					.addDomListener(
							el,
							"click",
							function(event) {
								var posy;
								if (event.pageY) {
									posy = event.pageY;
								} else if (event.clientX
										|| event.clientY) {
									posy = event.clientY
											+ document.body.scrollTop
											+ document.documentElement.scrollTop;
								}
								var zoomtop = zoombar
										.pagePosition(el)[1];
								var levels = Math
										.abs(Math
												.floor((zoomtop - posy) / 11));
								var newtop = zoombar.slideMinTop
										+ ((levels - 1) * 11);
								$D.setStyle(zoombar.slider,
										'top', newtop
										+ "px");
								map
								.setZoom(18 - levels - 1);
							});

					$W.Maps.Event.addDomListener(el, "mouseup",
							function(event) {
						zoombar.slider.startDrag = null;
						var diff = parseInt($D.getStyle(
								zoombar.slider, 'top'), 10)
								- parseInt($D.getStyle(el,
										'top'), 10);
						var level = Math.abs(Math
								.floor((diff) / 11));
						map.setZoom(18 - level - 1);
						var newtop = (level) * 11
								+ zoombar.slideMinTop;
						$D.setStyle(zoombar.slider, 'top',
								newtop + 'px');
					});
					$W.Maps.Event
					.addDomListener(
							el,
							"mousemove",
							function(event) {
								if (zoombar.slider.startDrag !== null
										&& typeof zoombar.slider.startDrag !== 'undefined') {
									var posy;
									if (event.pageY) {
										posy = event.pageY;
									} else if (event.clientY) {
										posy = event.clientY
												+ document.body.scrollTop
												+ document.documentElement.scrollTop;
									}
									var top = parseInt(
											$D
											.getStyle(
													zoombar.slider,
													'top'),
													10)
													- (zoombar.slider.startDrag - posy);
									$D.setStyle(
											zoombar.slider,
											'top', top
											+ "px");
									zoombar.slider.startDrag = posy;
								}
							});


					break;
				}
			},
			/**
			 * Sets the Slider position based on the Map Zoom
			 */

			initializeSlider : function() {
				var newtop = this.slideMinTop + this.oMap.getZoom()
						* 11;
				$D.setStyle(this.slider, 'top', newtop + "px");
			},
			initialize : function(map) {
				this.oMap = map;
				var el = document.createElement("div");
				this.container = el;
				$D.addClass(el, 'fleetzoombar');
				this.addButton($W.Maps.FleetZoomBar.PAN_EAST);
				this.addButton($W.Maps.FleetZoomBar.PAN_WEST);
				this.addButton($W.Maps.FleetZoomBar.PAN_NORTH);
				this.addButton($W.Maps.FleetZoomBar.PAN_SOUTH);
				this.addButton($W.Maps.FleetZoomBar.SLIDER);
				this.addButton($W.Maps.FleetZoomBar.ZOOM_BAR);
				this.addButton($W.Maps.FleetZoomBar.ZOOM_IN);
				this.addButton($W.Maps.FleetZoomBar.PAN_TO_CENTER);
				this.addButton($W.Maps.FleetZoomBar.ZOOM_OUT);
				$D.get(this.oMap.getContainer()).appendChild(el);
				this.slideMinTop = parseInt($D.getStyle(
						this._zoombar, 'top'), 10) + 7;
				this.slideMaxTop = this.slideMinTop
						+ parseInt($D.getStyle(this._zoombar,
								'height'), 10) - 12;
				this.initializeSlider();
				return this.container;
			},
			getDefaultPosition : function() {
				return new GControlPosition(G_ANCHOR_TOP_LEFT,
						new GSize(7, 7));
			},
			pagePosition : function(forElement) {
				var valueT = 0, valueL = 0;
				var element = forElement;
				var child = forElement;
				while (element) {
					if (element == document.body) {
						if ($D.getStyle(child, 'position') == 'absolute') {
							break;
						}
					}
					valueT += element.offsetTop || 0;
					valueL += element.offsetLeft || 0;
					child = element;
					try {
						// wrapping this in a try/catch because IE chokes on the offsetParent
						element = element.offsetParent;
					} catch (e) {
						break;
					}
				}
				element = forElement;
				while (element) {
					valueT -= element.scrollTop || 0;
					valueL -= element.scrollLeft || 0;
					element = element.parentNode;
				}
				return [ valueL, valueT ];
			}
			});
	$L.augmentProto($W.Maps.FleetZoomBar, GControl);
})();
