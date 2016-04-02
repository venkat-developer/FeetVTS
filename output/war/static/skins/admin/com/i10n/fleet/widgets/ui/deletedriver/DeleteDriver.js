(function() {
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var response = true;
	/**
	 * Delete Driver UI Widget
	 * 
	 * @author sabarish
	 */
	$W.DeleteDriver = function(el, oArgs) {
		$W.DeleteDriver.superclass.constructor.call(this, el, oArgs);
		this.addAdditionalListeners();

		this.setAutoCompleteDriverList();
	};
	$L
			.extend(
					$W.DeleteDriver,
					$W.DeleteEntity,
					{
						/* Defining call backs */
						oCallBacks : {
							oDeleteEntityCallBack : {
								success : function(o) {
									var responseData = o.responseText;
									var message = responseData.split(":");

									for ( var i = 0; i < message.length; i++) {

										if (message[i] != 0) {
											response = false;
											break;
										}
									}
									$D.getElementsByClassName("info", null,
											this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected drivers were successfully deleted.";
									this.setAutoCompleteDriverList();
								},
								failure : function(o) {
									$D.getElementsByClassName("info", null,
											this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected drivers failed.";
									this.setAutoCompleteDriverList();
								}
							},
							oReloadListCallBack : {
								success : function(o) {
									/* Destroying behavior layers */
									this.oListEventManager.destroy();
									this.elBase.innerHTML = o.responseText;
									/* Re-initializing */
									$W.DeleteDriver.superclass.constructor
											.call(this, this.elBase,
													this.oConfiguration);
									if (response == false) {

										$D.getElementsByClassName("info", null,
												this.elBase)[0].innerHTML = "<span class='title'>Failure:Some of the selected driver/s are involved in trip, Hence deletion failed.</span>";
										response = true;
									} else {

										$D.getElementsByClassName("info", null,
												this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected driver/s were successfully deleted.";
									}
									this.setAutoCompleteDriverList();
								},
								failure : function(o) {
									$D.getElementsByClassName("info", null,
											this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Unable to get data from the server.Try reloading the page";
									this.setAutoCompleteDriverList();
								}
							}
						},
						/**
						 * Default Search String
						 */
						DEFAULT_SEARCH_STRING : "Search...",

						/**
						 * Setups for showing autocomplete
						 */
						setAutoCompleteDriverList : function() {
							this._generateDataSourceDriverList();
							var autocompleteDriver = new $YW.AutoComplete(
									this.searchInputDriverList,
									$D
											.getElementsByClassName("search-autocomplete-driverlist")[0],
									this.oDataSourceDriverList);
							autocompleteDriver.prehighlightClassName = "yui-ac-prehighlight";
							autocompleteDriver.useShadow = true;
							this.deleteDriverListener();

						},

						/**
						 * Generates Datasource for autocomplete
						 */
						_generateDataSourceDriverList : function() {
							var dataSourceDriverList = [];

							var val = null;
							var result = new Array();
							var elList = YAHOO.util.Dom.getElementsByClassName(
									"tlist", null, this.elBase)[0];

							var cchild = YAHOO.util.Dom.getElementsByClassName(
									"l-col firstname first", null, elList,
									function(elTarget) {

										var re = elTarget.innerHTML;
										for ( var i = 0; i < re.length; i++) {
											val = re.split(">");

										}
										if (val[1] != null) {

											var data = {
												"name" : val[1]
											};
										}
										dataSourceDriverList.push(data);
									});

							var oDataSourceDriverList = null;
							this.oDataSourceDriverList = new $YU.LocalDataSource(
									dataSourceDriverList);
							this.oDataSourceDriverList.responseSchema = {
								fields : [ "name" ]
							};

						},
						/**
						 * Selects an item in the search with the given item
						 * name (object)
						 */
						selectItemDriverList : function(objectName) {
							/* Retaining the current scope */

							var val = null;
							var result = new Array();
							var elList = YAHOO.util.Dom.getElementsByClassName(
									"tlist", null, this.elBase)[0];

							var cchild = YAHOO.util.Dom
									.getElementsByClassName(
											"l-row entity-record slist-item",
											null,
											elList,
											function(elTarget) {
												YAHOO.util.Dom
														.getElementsByClassName(
																"l-col firstname first",
																null,
																elTarget,
																function(el) {

																	var re = el.innerHTML;
																	for ( var i = 0; i < re.length; i++) {
																		val = re
																				.split(">");

																	}

																	if (val[1] != objectName) {

																		YAHOO.util.Dom
																				.addClass(
																						elTarget,
																						"yui-hidden");

																	} else {
																		if (YAHOO.util.Dom
																				.hasClass(
																						elTarget,
																						"yui-hidden")) {
																			YAHOO.util.Dom
																					.removeClass(
																							elTarget,
																							"yui-hidden");
																		}
																	}
																});

											});
						},

						deleteDriverListener : function() {
							this.searchInputDriverList = $D
									.getElementsByClassName(
											"search-item-input-driverlist",
											null, this.elBase)[0];
							var enterKeyListenerUserList = new $YU.KeyListener(
									this.searchInputDriverList,
									{
										"keys" : 13
									},
									{
										"fn" : function() {
											this
													.selectItemDriverList(this.searchInputDriverList.value);
										},
										"scope" : this,
										"correctScope" : true
									});
							enterKeyListenerUserList.enable();
							$U.addDefaultInputText(this.searchInputDriverList,
									this.searchInputDriverList.value);
							/**
							 * Avoiding possible memory leaks;
							 */
							enterKeyListenerUserList = null;
						},

						addAdditionalListeners : function() {

							this.searchInputDriverList = $D
									.getElementsByClassName(
											"search-item-input-driverlist",
											null, this.elBase)[0];

						},
						/* Over riding necessary methods */
						reloadList : function() {
							/* Correcting the scope of the call back */
							this.oCallBacks.oReloadListCallBack.scope = this;
							var elList = $D.getElementsByClassName("tlist",
									null, this.elBase)[0];

							var cchild = $D.getElementsByClassName(
									"delete-entity-checkbox", null, elList,
									function(elTarget) {
										if (elTarget.checked == true) {

											elList.deleteRow(elTarget);

										}

									});

							$U.Connect
									.asyncRequest(
											'GET',
											"/fleet/view/controlpanel/?markup=DeleteDriver&body=true",
											this.oCallBacks.oReloadListCallBack);
						},
						deleteEntity : function(aKeys) {
							var sParams = "";
							sParams = aKeys.join(":");
							/* Hitting the server URL to delete the entities */
							this.oCallBacks.oDeleteEntityCallBack.scope = this;
							$U.Connect
									.asyncRequest(
											'GET',
											"/fleet/form/admin/?command_type=delete_driver&driverList="
													+ sParams,
											this.oCallBacks.oDeleteEntityCallBack,
											null);
						},
						refresh : function() {
							this.onCancel();
							$D
									.getElementsByClassName("info", null,
											this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> Before deleting a driver make sure that there is no active/paused trip for the selected driver.";
						}
					});
})();
