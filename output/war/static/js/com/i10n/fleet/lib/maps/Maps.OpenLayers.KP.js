(function() {
	var $O = OpenLayers;
	var $YU = YAHOO.util;
	var $D = YAHOO.util.Dom;
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $YW = YAHOO.widget;
	var $W = getPackageForName("com.i10n.fleet.widget.lib");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var kmlSelect;
	/**
	 * An Adapter Widget for Other Widgets to interact with OpenLayers Map API.
	 * @param {Object} el
	 * @param {Object} oArgs
	 * @author sabarish
	 */
	$W.Maps = function(el, oArgs) {
		if (!$L.isNull($O)) {
			this.initMap(el, oArgs);
		}
	};
	/**
	 * An Adapter to OpenLayer Maps
	 * @param {Object} div
	 * @param {Object} options
	 * @extends $O.Map
	 */
	$W.Maps.Map = function(div, options) {
		$W.Maps.Map.superclass.constructor.call(this, div, options);
	};
	$L.extend($W.Maps.Map, $O.Map, {
		/**
		 * An adapter function to fitBounds
		 * @param {Object} oBounds
		 */
		fitBounds: function(oBounds) {
		this.zoomToExtent(oBounds);
	}
	});
	$W.Maps.Layer = {};
	/**
	 * Class: $W.Maps.Layer.SphericalTMS
	 * A Class to access CustomTiles with a url base don x y and z.
	 *
	 * (code)
	 *     new $W.Maps.Layer.SphericalTMS("TMS",
	 *       "http://tah.openstreetmap.org/Tiles/tile/${z}/${x}/${y}.png");
	 * (end)
	 *
	 * This layer defaults to Spherical Mercator.
	 */
	$W.Maps.Layer.SphericalTMS = $O.Class($O.Layer.XYZ, {
		name: "Spherical TMS",
		attribution: "Maps Data by <a href='http://maps.google.com/'>Google Maps</a>",
		sphericalMercator: true,
		url: 'http://mt.google.com/vt/v=ap.106&hl=en&x=${x}&y=${y}&z=${z}&s=G',
		CLASS_NAME: "OpenLayers.Layer.SphericalTMS"
	});
	/**
	 * Class: $W.Maps.Layer.SphericalTMS
	 * A Class to access i10n tiles
	 *
	 * (code)
	 *     new $W.Maps.Layer.I10NTMS("i10n");
	 * (end)
	 *
	 * This layer defaults to Spherical Mercator.
	 */
	$W.Maps.Layer.I10NTMS = $O.Class($O.Layer.XYZ, {
		name: "i10n",
		attribution: "Maps Data by <a href='http://www.harman.com/'>Harman International</a>",
		sphericalMercator: true,

		url: 'http://10.7.9.40/vt/lbw/lyrs=m&hl=en&x=${x}&y=${y}&z=${z}&s=Galileo',

		getURL: function(oBounds) {
		var res = this.map.getResolution();
		var x = Math.round((oBounds.left - this.maxExtent.left) / (res * this.tileSize.w));
		var y = Math.round((this.maxExtent.top - oBounds.top) / (res * this.tileSize.h));
		var z = this.map.getZoom();
		var limit = Math.pow(2, z);
		var url = this.url;
		/*   z = 17 - z - 1;   */
		z=z;
		var s = '' + x + y + z;
		if (url instanceof Array) {
			url = this.selectUrl(s, url);
		}
		var path = $O.String.format(url, {
			'x': x,
			'y': y,
			'z': z
		});
		return path;
	},
	CLASS_NAME: "OpenLayers.Layer.I10NTMS"
	});
	/**
	 * Class: $W.Maps.Layer.SphericalTMS
	 * A Class to access i10n tiles
	 *
	 * (code)
	 *     new $W.Maps.Layer.I10NTMS("i10n");
	 * (end)
	 *
	 * This layer defaults to Spherical Mercator.
	 */
	$W.Maps.Layer.I10NTMSV2 = $O.Class($O.Layer.XYZ, {
		name: "i10n-v2",
		attribution: "Maps Data by <a href='http://www.harman.com/'>Harman International</a>",
		sphericalMercator: true,
		//   url: 'http://demo.gwtrack.com/maps2/${x}-${y}-${z}.png',
		url :  "http://khm.google.com/vt/lbw/lyrs=m&hl=en&x=${x}&y=${y}&z=${z}&s=Galileo",
		getURL: function(oBounds) {
		var res = this.map.getResolution();
		var x = Math.round((oBounds.left - this.maxExtent.left) / (res * this.tileSize.w));
		var y = Math.round((this.maxExtent.top - oBounds.top) / (res * this.tileSize.h));
		var z = this.map.getZoom();
		var limit = Math.pow(2, z);
		var url = this.url;
		// z = 17 - z;
		var s = '' + x + y + z;
		if (url instanceof Array) {
			url = this.selectUrl(s, url);
		}
		var path = $O.String.format(url, {
			'x': x,
			'y': y,
			'z': z
		});
		return path;
	},
	CLASS_NAME: "OpenLayers.Layer.I10NTMSV2"
	});
	/**
	 * Fleet Custom Class representing latitude and longitude.
	 * @extends $GM.LatLng
	 * @param {Object} oArgs
	 */
	$W.Maps.LatLng = function(oArgs) {
		var oLatLng = $O.Layer.SphericalMercator.forwardMercator(oArgs.lon, oArgs.lat);
		$W.Maps.LatLng.superclass.constructor.call(this, oLatLng.lon, oLatLng.lat);
	};
	$L.extend($W.Maps.LatLng, $O.LonLat);
	/**
	 * Custom Icon for adding Spriting functionalities.
	 * @param {Object} url
	 * @param {Object} size
	 * @param {Object} sprite
	 * @param {Object} offset
	 * @param {Object} calculateOffset
	 */
	$W.Maps.Icon = function(url, size, sprite, offset, calculateOffset) {
		$W.Maps.Icon.superclass.constructor.call(this, url, size, sprite, offset, calculateOffset);
	};
	$L.extend($W.Maps.Icon, $O.Icon, {
		/**
		 * initializer function
		 * @param {Object} url
		 * @param {Object} size
		 * @param {Object} sprite
		 * @param {Object} offset
		 * @param {Object} calculateOffset
		 */
		initialize: function(url, size, sprite, offset, calculateOffset) {
		this.url = url;
		this.size = (size) ? size : new $O.Size(20, 20);
		this.sprite = (sprite) ? sprite : new $O.Pixel(0, 0);
		this.offset = offset ? offset : new $O.Pixel(-(this.size.w / 2), -(this.size.h / 2));
		this.calculateOffset = calculateOffset;
		this.imageDiv = this._createImageDiv($O.Util.createUniqueID("OL_Icon_"));
	},
	_createImageDiv: function(id, oArgs) {
		var elDiv = document.createElement('div');
		elDiv.id = id;
		$D.addClass(elDiv, "map-marker-icon");
		$D.setStyle(elDiv, "width", this.size.w + "px");
		$D.setStyle(elDiv, "height", this.size.h + "px");
		$D.setStyle(elDiv, "background", "no-repeat url('" + this.url + "') " + (0 - this.sprite.x) + "px " + (0 - this.sprite.y) + "px");
		return elDiv;
	},
	/**
	 * Draws the icon on the layer.
	 * @param {Object} px
	 */
	draw: function(px) {
		$D.setStyle(this.imageDiv, "position", "absolute");
		this.moveTo(px);
		return this.imageDiv;
	},
	/**
	 * Move Icon to passed in px.
	 * @param {Object} px
	 */
	moveTo: function(px) {
		//if no px passed in, use stored location
		if (px !== null) {
			this.px = px;
		}
		if (this.imageDiv !== null) {
			if (this.px === null) {
				this.display(false);
			}
			else {
				if (this.calculateOffset) {
					this.offset = this.calculateOffset(this.size);
				}
				var offsetPx = this.px.offset(this.offset);
				$O.Util.modifyDOMElement(this.imageDiv, null, offsetPx);
			}
		}
	},
	/**
	 * Sets opacity of the div representing the object
	 * @param {Object} opacity
	 */
	setOpacity: function(opacity) {
		$D.setStyle(this.imageDiv, "opacity", opacity);
	},
	/**
	 * Returns a new object with the same properties as the current object
	 */
	clone: function() {
		return new $W.Maps.Icon(this.url, this.size, this.sprite, this.offset, this.calculateOffset);
	},
	/**
	 * Destroys the icon.
	 */
	destroy: function() {
		this.url = null;
		this.size = null;
		this.sprite = null;
		this.offset = null;
		this.calculateOffset = null;
		if (this.imageDiv && this.imageDiv.parentNode) {
			this.imageDiv.parentNode.removeChild(this.imageDiv);
		}
		this.imageDiv = null;
	}
	});
	$L.augmentObject($W.Maps, {
		"ATTR_MAP": "map",
		"ATTR_LAYERS": "layers",
		"ATTR_DEFAULT_LAYER": "defaultLayer",
		"DEFAULT_LATLNG": new $W.Maps.LatLng({
			lon: 77.581114,
			lat: 12.960398
		}),
		"DEFAULT_ZOOM": 7,
		"DEFAULT_LAYER": "I10N",
		"MARKER_SPRITE_CONFIG": {
		"vehicles": {
		"idle": new $W.Maps.Icon('/static/img/vehicles/blurb/idle.png'			,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/blurb/blurb.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/blurb/0360.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/blurb/45.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/blurb/90.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/blurb/135.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/blurb/180.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/blurb/225.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/blurb/270.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/blurb/315.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/blurb/offline.png'	,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/blurb/stopped.png'	,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"onlinestart": new $W.Maps.Icon('/static/img/maps/From.png'        		,new $O.Size(45,45), new $O.Pixel(0,0), new $O.Pixel(-20, -20)),
		"onlineend": new $W.Maps.Icon('/static/img/maps/To.png'            		,new $O.Size(45,45), new $O.Pixel(0,0), new $O.Pixel(-20, -20)),
		"trackpoint": new $W.Maps.Icon('/static/img/buttons/bullet.png'    		,new $O.Size(7,7), new $O.Pixel(0,0), new $O.Pixel(-5, -5)),
		"idlemapreport":new $W.Maps.Icon('/static/img/maps/pin-orange.png'		,new $O.Size(20,25), new $O.Pixel(0,0), new $O.Pixel(-10, -10))                
	},
	"custom-1":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/bike/idle.png'			,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/bike/bike.png'			,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/bike/0360.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/bike/45.png'			,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/bike/90.png'			,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/bike/135.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/bike/180.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/bike/225.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/bike/270.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/bike/315.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/bike/offline.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/bike/stopped.png'		,new $O.Size(39,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-2":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/blurb/idle.png'			,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/blurb/blurb.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/blurb/0360.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/blurb/45.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/blurb/90.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/blurb/135.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/blurb/180.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/blurb/225.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/blurb/270.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/blurb/315.png'		,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/blurb/offline.png'	,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/blurb/stopped.png'	,new $O.Size(20,35), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-3":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'				,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busblue/blue.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busblue/0360.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busblue/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busblue/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busblue/135.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busblue/180.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busblue/225.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busblue/270.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busblue/315.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/bustopped.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-4":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'				,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busbrown/brown.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busbrown/0360.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busbrown/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busbrown/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busbrown/135.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busbrown/180.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busbrown/225.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busbrown/270.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busbrown/315.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-5":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'							,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/busdarkyellow.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/0360.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/45.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/90.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/135.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/180.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/225.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/270.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busdarkyellow/315.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'					,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'					,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-6":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'				,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busgreen/green.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busgreen/0360.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busgreen/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busgreen/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busgreen/135.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busgreen/180.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busgreen/225.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busgreen/270.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busgreen/315.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'		,new $O.Size(45,45), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-7":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'				,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/buspink/pink.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/buspink/0360.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/buspink/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/buspink/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/buspink/135.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/buspink/180.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/buspink/225.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/buspink/270.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/buspink/315.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-8":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'				,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/buspurple/purple.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/buspurple/0360.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/buspurple/45.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/buspurple/90.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/buspurple/135.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/buspurple/180.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/buspurple/225.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/buspurple/270.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/buspurple/315.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-9":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'					,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busskyblue/skyblue.png'	,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busskyblue/0360.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busskyblue/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busskyblue/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busskyblue/135.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busskyblue/180.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busskyblue/225.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busskyblue/270.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busskyblue/315.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-10":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'					,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busviolet/violet.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busviolet/0360.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busviolet/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busviolet/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busviolet/135.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busviolet/180.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busviolet/225.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busviolet/270.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busviolet/315.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-11":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/busidle.png'					,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/busyellow/yellow.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/busyellow/0360.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/busyellow/45.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/busyellow/90.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/busyellow/135.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/busyellow/180.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/busyellow/225.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/busyellow/270.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/busyellow/315.png'		,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/busoffline.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/busstopped.png'			,new $O.Size(58,63), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-12":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/car/idle.png'				,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/car/car.png'				,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/car/0360.png'				,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/car/45.png'				,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/car/90.png'				,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/car/135.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/car/180.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/car/225.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/car/270.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/car/315.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/car/offline.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/car/stopped.png'			,new $O.Size(30,20), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-13":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/jeep/idle.png'				,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/jeep/jeep.png'				,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/jeep/0360.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/jeep/45.png'				,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/jeep/90.png'				,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/jeep/135.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/jeep/180.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/jeep/225.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/jeep/270.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/jeep/315.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/jeep/offline.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/jeep/stopped.png'			,new $O.Size(42,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-14":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/jeep1/idle.png'				,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/jeep1/jeep1.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/jeep1/0360.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/jeep1/45.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/jeep1/90.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/jeep1/135.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/jeep1/180.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/jeep1/225.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/jeep1/270.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/jeep1/315.png'			,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/jeep1/offline.png'		,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/jeep1/stopped.png'		,new $O.Size(38,29), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-15":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/pointer/idle.png'			,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/pointer/pointer.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/pointer/0360.png'			,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/pointer/45.png'			,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/pointer/90.png'			,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/pointer/135.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/pointer/180.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/pointer/225.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/pointer/270.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/pointer/315.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/pointer/offline.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/pointer/stopped.png'		,new $O.Size(15,44), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-16":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/tractor/idle.png'			,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/tractor/tractor.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/tractor/0360.png'			,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/tractor/45.png'			,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/tractor/90.png'			,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/tractor/135.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/tractor/180.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/tractor/225.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/tractor/270.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/tractor/315.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/tractor/offline.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/tractor/stopped.png'		,new $O.Size(40,36), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-17":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/truck/idle.png'				,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/truck/truck.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/truck/0360.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/truck/45.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/truck/90.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/truck/135.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/truck/180.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/truck/225.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/truck/270.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/truck/315.png'			,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/truck/offline.png'		,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/truck/stopped.png'		,new $O.Size(40,30), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-18":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/van/idle.png'				,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/van/van.png'				,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/van/0360.png'				,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/van/45.png'				,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/van/90.png'				,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/van/135.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/van/180.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/van/225.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/van/270.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/van/315.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/van/offline.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/van/stopped.png'			,new $O.Size(40,31), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	},
	"custom-19":{
		"idle": new $W.Maps.Icon('/static/img/vehicles/fueltanker/idle.png'				,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online": new $W.Maps.Icon('/static/img/vehicles/fueltanker/fueltanker.png'		,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online0": new $W.Maps.Icon('/static/img/vehicles/fueltanker/0360.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online45": new $W.Maps.Icon('/static/img/vehicles/fueltanker/45.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online90": new $W.Maps.Icon('/static/img/vehicles/fueltanker/90.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online135": new $W.Maps.Icon('/static/img/vehicles/fueltanker/135.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online180": new $W.Maps.Icon('/static/img/vehicles/fueltanker/180.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online225": new $W.Maps.Icon('/static/img/vehicles/fueltanker/225.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online270": new $W.Maps.Icon('/static/img/vehicles/fueltanker/270.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"online315": new $W.Maps.Icon('/static/img/vehicles/fueltanker/315.png'			,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"offline": new $W.Maps.Icon('/static/img/vehicles/fueltanker/offline.png'		,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34)),
		"stopped": new $W.Maps.Icon('/static/img/vehicles/fueltanker/stopped.png'		,new $O.Size(40,50), new $O.Pixel(0,0), new $O.Pixel(-18, -34))
	}
	}
	});
	$L.augmentObject($W.Maps, {
		"LAYERS": {
		/*"GoogleStreetsTiles": {
                fn: $W.Maps.Layer.SphericalTMS,
                args: ["Google Streets", ["http://mt0.google.com/vt/v=ap.106&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt1.google.com/vt/v=ap.106&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt2.google.com/vt/v=ap.106&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt3.google.com/vt/v=ap.106&hl=en&x=${x}&y=${y}&z=${z}&s=G"], {
                    numZoomLevels: 20
                }]
            },*/
		"GooglePhysicalTiles": {
		fn: $W.Maps.Layer.SphericalTMS,
		args: ["Google Physical", ["http://mt0.google.com/vt/v=app.106&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt1.google.com/vt/v=app.106&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt2.google.com/vt/v=app.106&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt3.google.com/vt/v=app.106&hl=en&x=${x}&y=${y}&z=${z}&s=G"], {
			numZoomLevels: 20
		}]
	},
	/*"GoogleHybridTiles": {
                fn: $W.Maps.Layer.SphericalTMS,
                args: ["Google Hybrid", ["http://mt0.google.com/vt/v=apt.106&hl=en&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt1.google.com/vt/v=apt.106&hl=en&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt2.google.com/vt/v=apt.106&hl=en&hl=en&x=${x}&y=${y}&z=${z}&s=G", "http://mt3.google.com/vt/v=apt.106&hl=en&hl=en&x=${x}&y=${y}&z=${z}&s=G"], {
                    numZoomLevels: 20
                }]
            },*/
	"GoogleSatelliteTiles": {
		fn: $W.Maps.Layer.SphericalTMS,
		args: ["Google Satellite", ["http://khm0.google.com/kh/v=44&x=${x}&y=${y}&z=${z}&s=G", "http://khm1.google.com/kh/v=44&x=${x}&y=${y}&z=${z}&s=G", "http://khm2.google.com/kh/v=44&x=${x}&y=${y}&z=${z}&s=G", "http://khm3.google.com/kh/v=44&x=${x}&y=${y}&z=${z}&s=G"], {
			numZoomLevels: 20
		}]
	},
	"I10N": {
		fn: $W.Maps.Layer.I10NTMS,
		args: ["i10n"]
	},
	"I10NTMSV2": {
		fn: $W.Maps.Layer.I10NTMSV2,
		args: ["i10n-v2"]
	}
	}
	});
	/*try {
		if ($L.isFunction(GMap2)) {
			$L.augmentObject($W.Maps.LAYERS, {
				"GoogleStreets": {
				fn: $O.Layer.Google,
				args: ["Google Streets", {
					numZoomLevels: 20,
					sphericalMercator: true
				}]
			},
			"GooglePhysical": {
				fn: $O.Layer.Google,
				args: ["Google Physical", {
					type: G_PHYSICAL_MAP,
					sphericalMercator: true
				}]
			},
			"GoogleHybrid": {
				fn: $O.Layer.Google,
				args: ["Google Hybrid", {
					type: G_HYBRID_MAP,
					numZoomLevels: 20,
					sphericalMercator: true
				}]
			},
			"GoogleSatellite": {
				fn: $O.Layer.Google,
				args: ["Google Satellite", {
					type: G_SATELLITE_MAP,
					numZoomLevels: 20,
					sphericalMercator: true
				}]
			}
			});
		}
	} 
	catch (ex) {
		//GMap Not Loaded.
	}*/
	$L.extend($W.Maps, $YU.AttributeProvider);
	$L.augmentObject($W.Maps.prototype, {
		/**
		 * Initializes the Attributes
		 */
		initAttributes: function() {
		/**
		 * @attribute map
		 * @description Current Map represented by the object.
		 * @type Object
		 */
		this.setAttributeConfig($W.Maps.ATTR_MAP, {
			value: null
		});
		/**
		 * @attribute layers
		 * @description Current Layers supported by the map instance
		 * @type Array
		 */
		this.setAttributeConfig($W.Maps.ATTR_LAYERS, {
			value: []
		});
		/**
		 * @attribute default layer
		 * @description Default Layer Configured for the map.
		 * @type String
		 */
		this.setAttributeConfig($W.Maps.ATTR_DEFAULT_LAYER, {
			value: null
		});
	},
	/**
	 * Initializes the Map.
	 * @param {Object} el
	 * @param {Object} oArgs
	 */
	initMap: function(el, oArgs) {
		this.initAttributes();
		this._setConfiguredAttributes(el);
		var oOptions = {
				controls: [new $O.Control.Navigation(), new $W.Maps.FleetPanZoomBar(), new $W.Maps.FleetLayerSwitcher(), new $O.Control.ArgParser(), new $O.Control.Attribution()],
				projection: new $O.Projection("EPSG:900913"),
				displayProjection: new $O.Projection("EPSG:4326"),
				units: "m",
				numZoomLevels: 18,
				maxResolution: 156543.0339,
				maxExtent: new $O.Bounds(-20037508, -20037508, 20037508, 20037508.34)
		};
		var oMap = new $W.Maps.Map(el, oOptions);
		this.set($W.Maps.ATTR_MAP, oMap);
		this._initLayers();
		var oCenterLatLng = $W.Maps.DEFAULT_LATLNG;
		oMap.setCenter(oCenterLatLng, $W.Maps.DEFAULT_ZOOM);
	},
	/**
	 * a need for GMaps V2. so an interface created for it.
	 */
	checkResize: function() {
	},
	_setConfiguredAttributes: function(elContainer) {
		var sLayerAttr = $D.getAttribute(elContainer, "layers");
		if (!sLayerAttr) {
			sLayerAttr = $W.Maps.DEFAULT_LAYER;
		}
		var aLayers = sLayerAttr.split("|");
		this.set($W.Maps.ATTR_LAYERS, aLayers);
		var sDefaultLayer = $D.getAttribute(elContainer, "defaultLayer");
		if (!(sDefaultLayer && $U.Arrays.contains(aLayers, sDefaultLayer))) {
			sDefaultLayer = aLayers[0];
		}
		this.set($W.Maps.ATTR_DEFAULT_LAYER, sDefaultLayer);
	},
	_initLayers: function() {
		var aLayers = this.get($W.Maps.ATTR_LAYERS);
		var aLayerObj = [];
		for (var i = 0; i < aLayers.length; i++) {
			var oLayerConfig = $W.Maps.LAYERS[aLayers[i]];
			var fnNewLayerObj = function() {
			};
			$L.extend(fnNewLayerObj, oLayerConfig.fn);
			var oLayer = new fnNewLayerObj();
			aLayerObj.push(oLayer);
			fnNewLayerObj.superclass.constructor.apply(oLayer, oLayerConfig.args);
			oLayerConfig = null;
			oLayer = null;
		}
		this.get($W.Maps.ATTR_MAP).addLayers(aLayerObj);
		aLayerObj = null;
		aLayers = null;
	},
	/** 
	 * Method: destroy
	 * Nullify references and remove event listeners to prevent circular
	 * references and memory leaks
	 */
	destroy: function() {
		// Erase any drawn elements
		this.erase();
		$O.Event.stopObservingElement(this.imageDiv);
		this.imageDiv.innerHTML = "";
		this.imageDiv = null;
	}
	});
	/**
	 * Custom Layer for Info Windows.
	 */
	$W.Maps.InfoWindowLayer = $O.Class($O.Layer, {
		/** 
		 * APIProperty: isBaseLayer
		 * {Boolean} Markers layer is never a base layer.
		 */
		isBaseLayer: false,
		infoWindows: null,
		/**
		 * Initializer Method
		 * @param {Object} name
		 * @param {Object} options
		 */
		initialize: function(name, options) {
		$O.Layer.prototype.initialize.apply(this, arguments);
		this.infoWindows = [];
	},
	/**
	 * Adds The passed info window to the current and renders it.
	 * @param {Object} oInfoWindow
	 */
	addInfoWindow: function(oInfoWindow) {
		this.infoWindows.push(oInfoWindow);
		this.drawInfoWindow(oInfoWindow);
	},
	/**
	 * Renders the info window in the layer.
	 * @param {Object} oInfoWindow
	 */
	drawInfoWindow: function(oInfoWindow) {
		var elInfo = null;
		if (oInfoWindow.oPosition) {
			var oPixel = this.map.getLayerPxFromLonLat(oInfoWindow.oPosition);
			if (oPixel === null) {
				oInfoWindow.display(false);
			}
			else {
				if (!oInfoWindow.isDrawn()) {
					elInfo = oInfoWindow.draw(oPixel);
					this.div.appendChild(elInfo);
				}
				else {
					oInfoWindow.moveTo(oPixel);
				}
			}
		}
		else {
			elInfo = oInfoWindow.draw();
			this.div.appendChild(elInfo);
			oInfoWindow.display(false);
		}
	},
	/**
	 * Moves all the info windows based on the bounds and zoom.
	 * @param {Object} bounds
	 * @param {Object} zoomChanged
	 * @param {Object} dragging
	 */
	moveTo: function(bounds, zoomChanged, dragging) {
		$O.Layer.prototype.moveTo.apply(this, arguments);
		if (zoomChanged || !this.drawn) {
			for (var i = 0, len = this.infoWindows.length; i < len; i++) {
				this.drawInfoWindow(this.infoWindows[i]);
			}
			this.drawn = true;
		}
	}
	});
	/**
	 * Custom Info Window for the application
	 */
	$W.Maps.FleetInfoWindow = $O.Class({
		events: null,
		/**
		 * Map where this info window is displayed
		 */
		oMap: null,
		/**
		 * Current position in LonLat where this InfoWindow is displayed.
		 */
		oPosition: null,
		/**
		 * Pixels to offset the info window while displaying it.
		 */
		OFFSET: new $O.Pixel(0, -322),
		/**
		 * Top Right Offset to calculate bounds while panning.
		 */
		PAN_TR_OFFSET: new $O.Pixel(300, -330),
		/**
		 * Bottom Left Offset to calculate bounds while panning.
		 */
		PAN_BL_OFFSET: new $O.Pixel(-5, -5),
		/**
		 * Initializer Method
		 * @param {Object} oArgs
		 */

		initialize: function(oArgs) {
		this.oMap = oArgs.map;
		this.elBase = this._createElement();
		this._oTabView = new $YW.TabView(this.elBase);
		$E.addListener($D.getElementsByClassName("close-button", null, this.elBase), "click", function(oArgs, oArgsSelf) {
			this.display(false);
		}, this, true);
		var oLayer = this.oMap.getLayersByName("InfoWindow")[0];
		if (!oLayer) {

			var kmlLayer = new OpenLayers.Layer.Vector("KML", {
				projection: new OpenLayers.Projection("EPSG:4326"),
				displayProjection: new OpenLayers.Projection("EPSG:4326"),
				strategies: [new OpenLayers.Strategy.Fixed()],
				protocol: new OpenLayers.Protocol.HTTP({
					url: "/static/kml/lines.kml",
					format: new OpenLayers.Format.KML({
						extractStyles: true,
						extractAttributes: true,
						maxDepth: 2
					})
				})
			});
			this.oMap.addLayer(kmlLayer);
			kmlSelect = new OpenLayers.Control.SelectFeature(kmlLayer);

			kmlLayer.events.on({
				"featureselected": 
					function(event) {
				var feature = event.feature;
				var selectedFeature = feature;
				var popup = new OpenLayers.Popup.FramedCloud("chicken",
						feature.geometry.getBounds().getCenterLonLat(),
						new OpenLayers.Size(100,100),
						"<h2>"+feature.attributes.name + "</h2>" + feature.attributes.description,
						null, true, function(evt) { kmlSelect.unselectAll(); }
				);
				feature.popup = popup;
				this.map.addPopup(popup);
			}
			,
			"featureunselected": 	function (event) {
				var feature = event.feature;
				if(feature.popup) {
					this.map.removePopup(feature.popup);
					feature.popup.destroy();
					delete feature.popup;
				}
			}
			});
			this.oMap.addControl(kmlSelect);
			kmlSelect.deactivate();
			
			oLayer = new $W.Maps.InfoWindowLayer("InfoWindow");
			this.oMap.addLayer(oLayer);
			
			this.oMap.addControl(new OpenLayers.Control.LayerSwitcher());

			oLayer.setZIndex(352);
		}
		oLayer.addInfoWindow(this);

	},
	/**
	 * Sets the contents of the tabs based on the arguments passed
	 * @param {Object} oArgs
	 */
	setContent: function(oArgs) {
		if (oArgs && oArgs.content) {
			for (var sTabId in oArgs.content) {
				var elTabContainer = $D.getElementsByClassName("tabcontent-" + sTabId, null, this.elBase)[0];
				if (elTabContainer) {
					elTabContainer.innerHTML = oArgs.content[sTabId];
				}
			}
		}
	},
	/**
	 * Method used by users to move or open the InfoWindow
	 * @param {Object} oArgs
	 */
	drawAndMoveTo: function(oArgs) {
		this.setContent(oArgs);
		this.oPosition = oArgs.position;
		var oPixel = this.oMap.getLayerPxFromLonLat(this.oPosition);
		this.draw(oPixel);
		this.display(true);
		this.panMap();
	},
	_createElement: function() {
		var elTemplateContainer = $D.get("template-info-window");
		var elBase = ($D.getElementsByClassName("marker-info-window", null, elTemplateContainer)[0]).cloneNode(true);
		elBase.style.display = 'none';
		return elBase;
	},
	/**
	 * Destroys this object
	 */
	destroy: function() {
		this._oTabView.destroy();
		$E.purgeElement(this.elBase, true);
		this.erase(this.elBase);
		this.elBase = null;
		this.oMap = null;
		this.oPosition = null;
	},
	/**
	 * Pans the map to display the info window optimally.
	 */
	panMap: function() {
		var oBounds = this.calculateBounds();
		var oCurrentBounds = this.oMap.getExtent();
		if (!oCurrentBounds.containsBounds(oBounds)) {
			this.oMap.panTo(oBounds.getCenterLonLat());
		}
	},
	/**
	 * Calculate Bounds to display the info windows
	 */
	calculateBounds: function() {
		var oPosition = this.oPosition;
		var oPosPixel = this.oMap.getPixelFromLonLat(oPosition);
		var oBottomLeft = oPosPixel.offset(this.PAN_BL_OFFSET);
		var oTopRight = oPosPixel.offset(this.PAN_TR_OFFSET);
		var oBounds = new $O.Bounds();
		oBounds.extend(this.oMap.getLonLatFromPixel(oBottomLeft));
		oBounds.extend(this.oMap.getLonLatFromPixel(oTopRight));
		return oBounds;
	},
	/**
	 * Draws the Info Window
	 * @param {Object} px
	 */
	draw: function(px) {
		if (px) {
			this.moveTo(px);
		}
		return this.elBase;
	},
	/**
	 * Erases the elements in the object
	 */
	erase: function() {
		if (this.elBase) {
			if (this.elBase.parentNode) {
				this.elBase.parentNode.removeChild(this.elBase);
			}
			this.elBase.innerHTML = "";
		}
	},
	/**
	 * Moves the info window to the required position
	 * @param {Object} px
	 */
	moveTo: function(px) {
		if (px !== null) {
			this.px = px;
		}
		if (this.elBase !== null) {
			if (this.px === null) {
				this.display(false);
			}
			else {
				$O.Util.modifyDOMElement(this.elBase, null, this.px.offset(this.OFFSET));
			}
		}
	},
	/**
	 * Checks where the current info window is already drawn.
	 */
	isDrawn: function() {
		var isDrawn = (this.elBase && this.elBase.parentNode && (this.elBase.parentNode.nodeType != 11));
		return isDrawn;
	},
	/**
	 * Checks whether the current info window is in screen.
	 */
	onScreen: function() {
	},
	/**
	 * sets/resets the display property of the div.
	 * @param {Object} display
	 */
	display: function(display) {
		var sDisplay = "none";
		if (display) {
			sDisplay = "block";
		}
		$D.setStyle(this.elBase, "display", sDisplay);
	},
	CLASS_NAME: "OpenLayers.InfoWindow"
	});
	/**
	 * Fleet Custom Marker.
	 * @extends $GM.Marker
	 * @param {Object} oArgs
	 */
	$W.Maps.Marker = function(oArgs) {
		if (oArgs.map) {
			var oMap = oArgs.map;
			var aMarkerLayers = oMap.getLayersByName("Markers");
			var oMarkerLayers = null;
			if (!aMarkerLayers || (0 === aMarkerLayers.length)) {
				oMarkerLayers = new $O.Layer.Markers("Markers");
				oMap.addLayer(oMarkerLayers);
				oMarkerLayers.setZIndex(351);
			}
			else {
				oMarkerLayers = aMarkerLayers[0];
			}
			var oConfig = $U.cloneObject(oArgs);
			var oOptions = oArgs.options;
			var oIcon = $W.Maps.MARKER_SPRITE_CONFIG[oOptions.iconType][oOptions.iconSubType];
			$O.Marker.call(this, oArgs.position, oIcon.clone());
			oMarkerLayers.addMarker(this);
			this._oMap = oMap;
		}
	};
	$L.augmentProto($W.Maps.Marker, $O.Marker);
	$L.augmentObject($W.Maps.Marker.prototype, {
		getPosition: function() {
		return this.lonlat;
	},
	clear: function() {
		var aMarkerLayers = this._oMap.getLayersByName("Markers");
		if (aMarkerLayers && (0 < aMarkerLayers.length)) {
			aMarkerLayers[0].removeMarker(this);
		}
		this.destroy();
	},
	destroy: function() {
		this.erase();
		this.map = null;
		this._oMap = null;
		if (this.events) {
			this.events.destroy();
			this.events = null;
		}
		if (this.icon) {
			this.icon.destroy();
			this.icon = null;
		}
	}
	}, true);
	/**
	 * Fleet Custom Class representing latitude and longitude bounds.
	 * @extends $O.Bounds
	 * @param {Object} oArgs
	 */
	$W.Maps.LatLngBounds = function(oArgs) {
		$W.Maps.LatLngBounds.superclass.constructor.call(this);
		if (oArgs) {
			this.extend(oArgs.sw);
			this.extend(oArgs.ne);
			this.extend(oArgs.nw);
			this.extend(oArgs.se);
		}
	};
	$L.extend($W.Maps.LatLngBounds, $O.Bounds);
	/**
	 *
	 * Event Adapter for Map Events.
	 */
	$W.Maps.Event = {
			/**
			 * Is A Static Class;
			 */
			/**
			 * Adapter to add Listeners to OpenLayer Objects
			 * @param {Object} oMarker
			 * @param {Object} sEvt
			 * @param {Object} fnHandler
			 */
			addListener: function(oMarker, sEvt, fnHandler) {
		var oMap = oMarker.map;
		if (oMarker.events) {
			oMarker.events.register(sEvt, oMarker, function(oEvt) {
				fnHandler.call(this, oEvt);
				$O.Event.stop(oEvt);
			});
		}
	},
	/**
	 * Adapter to trigger events on the OpneLayer Objects
	 * @param {Object} obj
	 * @param {Object} sType
	 */
	trigger: function(obj, sType) {
		if (obj.events) {
			obj.events.triggerEvent(sType, obj.events);
		}
	}
	};
	/**
	 * An adapter for Openlayers Polyline
	 * @param {Object} oArgs
	 */
	$W.Maps.PolyLine = function(oArgs) {
		if (!oArgs) {
			oArgs = {};
		}
		var sColor = (oArgs.color) ? oArgs.color : "#0000FF";
		var nWeight = (oArgs.weight) ? oArgs.weight : 5;
		var nOpacity = (oArgs.opacity) ? oArgs.opacity : 0.5;
		var aPoints = [];
		for (var i = 0; i < oArgs.positions.length; i++) {
			aPoints.push(new $O.Geometry.Point(oArgs.positions[i].lon, oArgs.positions[i].lat));
		}
		$W.Maps.PolyLine.superclass.constructor.call(this, aPoints);
		var oVector = new $O.Layer.Vector("Route!");
		oVector.addFeatures([new $O.Feature.Vector(this, null, {
			strokeColor: sColor,
			strokeOpacity: nOpacity,
			strokeWidth: nWeight,
			pointRadius: 5,
			pointerEvents: "visiblePainted"
		})]);
		if (oArgs.map) {
			oArgs.map.addLayer(oVector);
			this._oMap = oArgs.map;
		}
		$D.setStyle(oVector.div, "zIndex", 350);
		$D.setStyle(oVector.renderer.root, "zIndex", 350);
		this._oVector = oVector;
	};
	$L.extend($W.Maps.PolyLine, $O.Geometry.LineString, {
		/**
		 * Clears this layer from the map.
		 */
		clear: function() {
		this._oMap.removeLayer(this._oVector);
		this._oMap = null;
		this._oVector = null;
	}
	});
	/**
	 * A Custom Fleet ZoomBar for OpenLayers
	 * @extends $O.Control.PanZoomBar
	 */
	$W.Maps.FleetPanZoomBar = function() {
		$W.Maps.FleetPanZoomBar.superclass.constructor.call(this);
	};
	$L.extend($W.Maps.FleetPanZoomBar, $O.Control.PanZoomBar, {
		initialize: function() {
		$O.Control.PanZoom.prototype.initialize.apply(this, arguments);
	},
	panToCenter: function() {
		this.map.setCenter($W.Maps.DEFAULT_LATLNG);
	},
	/**
	 * Adds ZoomBar
	 * @param {Object} centered
	 */
	_addZoomBar: function(centered) {
		var id = this.id + "_" + this.map.id;
		var zoomsToEnd = this.map.getNumZoomLevels() - 1 - this.map.getZoom();
		var pos = centered.add(-1, zoomsToEnd * this.zoomStopHeight);
		var slider = document.createElement("div");
		$D.addClass(slider, "slider");
		$D.setStyle(slider, 'top', pos.y + 'px');
		this.slider = slider;
		this.sliderEvents = new $O.Events(this, slider, null, true, {
			includeXY: true
		});
		this.sliderEvents.on({
			"mousedown": this.zoomBarDown,
			"mousemove": this.zoomBarDrag,
			"mouseup": this.zoomBarUp,
			"dblclick": this.doubleClick,
			"click": this.doubleClick
		});
		var sz = new $O.Size();
		sz.h = this.zoomStopHeight * this.map.getNumZoomLevels();
		sz.w = this.zoomStopWidth;
		var div = null;
		div = document.createElement("div");
		$D.addClass(div, "zoombar");
		$D.setStyle(div, 'height', sz.h + 'px');
		$D.setStyle(div, 'width', sz.w + 'px');
		$D.setStyle(div, 'top', '111px');
		this.zoombarDiv = div;
		this.divEvents = new $O.Events(this, div, null, true, {
			includeXY: true
		});
		this.divEvents.on({
			"mousedown": this.divClick,
			"mousemove": this.passEventToSlider,
			"dblclick": this.doubleClick,
			"click": this.doubleClick
		});
		this.div.appendChild(div);
		this.startTop = 105;
		this.div.appendChild(slider);
		this.map.events.register("zoomend", this, this.moveZoomBar);
		centered = centered.add(0, this.zoomStopHeight * this.map.getNumZoomLevels());
		return centered;
	},
	/**
	 * Remove the button and corresponding listeners
	 * Updates the button array
	 * @param {Object} btn
	 */
	_removeButton: function(btn) {
		var parent = this.div;
		if (this.panToCenterEl) {
			$O.Event.stopObservingElement(this.panToCenterEl);
			parent.removeChild(this.panToCenterEl);
			this.panToCenterEl = null;
		}
		$O.Event.stopObservingElement(btn);
		btn.map = null;
		parent.removeChild(btn);
		$O.Util.removeItem(this.buttons, btn);
	},
	/**
	 * Overrides the base Class Method
	 * We ignore opacity,delayDisplay,etc
	 * @param {Object} id
	 * @param {Object} px
	 * @param {Object} sz
	 * @param {Object} imgURL
	 * @param {Object} position
	 * @param {Object} border
	 * @param {Object} sizing
	 * @param {Object} opacity
	 * @param {Object} delayDisplay
	 */
	_addButton: function(id, px, sz, imgURL, position, border, sizing, opacity, delayDisplay) {
		var parent = this.div;
		$D.addClass(parent, "fleetzoombar");
		var btn = null;
		if ($L.isUndefined(this.panToCenterEl) || $L.isNull(this.panToCenterEl)) {
			btn = document.createElement("div");
			$D.addClass(btn, "panToCenter");
			this.panToCenterEl = btn;
			parent.appendChild(btn);
			$O.Event.observe(btn, "click", $O.Function.bindAsEventListener(this.panToCenter, this));
			btn = null;
		}
		btn = document.createElement("div");
		$D.addClass(btn, id);
		parent.appendChild(btn);
		$O.Event.observe(btn, "mousedown", $O.Function.bindAsEventListener(this.buttonDown, btn));
		$O.Event.observe(btn, "dblclick", $O.Function.bindAsEventListener(this.doubleClick, btn));
		$O.Event.observe(btn, "click", $O.Function.bindAsEventListener(this.doubleClick, btn));
		btn.action = id;
		btn.map = this.map;
		if (!this.slideRatio) {
			var slideFactorPixels = this.slideFactor;
			var getSlideFactor = function() {
				return slideFactorPixels;
			};
		}
		else {
			var slideRatio = this.slideRatio;
			getSlideFactor = function(dim) {
				return this.map.getSize()[dim] * slideRatio;
			};
		}
		btn.getSlideFactor = getSlideFactor;
		getSlideFactor = null;
		this.buttons.push(btn);
		return btn;
	}
	});
	/**
	 * A Custom Fleet LayerSwitcher for OpenLayers
	 * @extends $O.Control.LayerSwitcher
	 */
	$W.Maps.FleetLayerSwitcher = function() {
		$W.Maps.FleetLayerSwitcher.superclass.constructor.call(this);
	};
	$L.extend($W.Maps.FleetLayerSwitcher, $O.Control.LayerSwitcher, {
		/**
		 * APIMethod: destroy
		 */
		destroy: function() {
		$O.Event.stopObservingElement(this.div);
		//clear out layers info and unregister their events 
		this.clearLayersArray();
		this.map.events.un({
			"addlayer": this.redraw,
			"changelayer": this.redraw,
			"removelayer": this.redraw,
			"changebaselayer": this.redraw,
			scope: this
		});
		OpenLayers.Control.prototype.destroy.apply(this, arguments);
	},
	/**
	 * loads the contents into div
	 */
	loadContents: function() {
		var baseEl = this.div;
		$D.addClass(baseEl, 'layerswitcher');
		var layers = this.map.layers;
		var layerEl = null;
		var parent = this.div;
		var count = 0;
		this.baseLayers = [];
		var grpdiv = this.baseLayers;
		for (var i = layers.length - 1; i >= 0; i--) {
			var layer = layers[i];
			if (layer.isBaseLayer) {
				layerEl = $O.Util.createDiv(layer.name, null, null, null, null, "1px solid black");
				layerEl.innerHTML = layer.name;
				$D.addClass(layerEl, 'switchlayers ' + layer.name);
				$D.setStyle(layerEl, 'right', count + 'px');
				if (layer == this.map.baseLayer) {
					$D.setStyle(layerEl, 'border', '1px solid black');
					$D.setStyle(layerEl, 'font-weight', 'bold');
				}
				else {
					$D.setStyle(layerEl, 'border', '1px solid white');
				}
				count = count + 57;
				var context = {
						id: layer.name,
						executectx: this
				};
				$O.Event.observe(layerEl, "click", $O.Function.bindAsEventListener(this.onInputClick, context));
				grpdiv.push({
					layer: layer,
					div: layerEl
				});
				parent.appendChild(layerEl);
				layerEl = null;
				context = null;
			}
		}
		return parent;
	},
	showControls: function() {
	},
	/**
	 * Based on the selection sets the baseLayers
	 * @param {Object} e
	 */
	onLayerSwitch: function(e) {
		var layerid = this.id;
		this.executectx.map.setBaseLayer(this.executectx.map.getLayersByName(layerid)[0]);
	},
	/** 
	 * Method: clearLayersArray
	 * clear all the corresponding listeners, the div, and reinitialize a new array.
	 *
	 */
	clearLayersArray: function() {
		var layers = this.baseLayers;
		for (var i = 0; i < layers.length; i++) {
			var layer = layers[i];
			$O.Event.stopObservingElement(layer.div);
			this.div.removeChild(layer.div);
		}
		this.baseLayers = [];
	},
	/** 
	 * Method: redraw
	 * Goes through and takes the current state of the Map and rebuilds the display
	 * Returns:
	 * {DOMElement} A reference to the DIV DOMElement containing the control
	 */
	redraw: function() {
		if (!this.checkRedraw()) {
			return this.div;
		}
		var len = this.map.layers.length;
		this.layerStates = new Array(len);
		for (i = 0; i < len; i++) {
			var layer = this.map.layers[i];
			this.layerStates[i] = {
					'name': layer.name,
					'visibility': layer.visibility,
					'inRange': layer.inRange,
					'id': layer.id
			};
		}
		this.clearLayersArray();
		var layers = this.map.layers.slice();
		if (!this.ascending) {
			layers.reverse();
		}
		var parent = this.div;
		for (var i = layers.length - 1, count = 0; i >= 0; i--) {
			layer = layers[i];
			var grpLayers = this.baseLayers;
			if (layer.isBaseLayer) {
				var layerEl = $O.Util.createDiv(layer.name, null, null, null, null, "1px solid black");
				layerEl.innerHTML = layer.name;
				$D.addClass(layerEl, 'switchlayers ' + layer.name);
				$D.setStyle(layerEl, 'right', count + 'px');
				if (layer == this.map.baseLayer) {
					$D.setStyle(layerEl, 'border', '1px solid black');
					$D.setStyle(layerEl, 'font-weight', 'bold');
				}
				else {
					$D.setStyle(layerEl, 'border-bottom-color', 'white');
					$D.setStyle(layerEl, 'border-top-color', 'white');
					$D.setStyle(layerEl, 'font-weight', 'normal');
				}
				count = count + 57;
				var context = {
						id: layer.name,
						executectx: this
				};
				$O.Event.observe(layerEl, "click", $O.Function.bindAsEventListener(this.onLayerSwitch, context));
				grpLayers.push({
					layer: layer,
					div: layerEl
				});
				parent.appendChild(layerEl);
				layerEl = null;
				context = null;
			}
			$D.setStyle(parent, 'width', count + 'px');
		}
		return parent;
	},
	draw: function() {
		$O.Control.prototype.draw.apply(this);
		// create layout divs
		this.loadContents();
		// populate div with current info
		this.redraw();
		return this.div;
	}
	});
})();