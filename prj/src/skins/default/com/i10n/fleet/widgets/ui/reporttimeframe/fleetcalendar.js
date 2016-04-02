(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    $W.IntervalCalendar = function(container, cfg){
        this._iState = 0;
        this.cfg = cfg ||
        {};
        this.cfg.multi_select = true;
        $W.IntervalCalendar.superclass.constructor.call(this, container, cfg);
        this.beforeSelectEvent.subscribe(this._intervalOnBeforeSelect, this, true);
        this.selectEvent.subscribe(this._intervalOnSelect, this, true);
        this.beforeDeselectEvent.subscribe(this._intervalOnBeforeDeselect, this, true);
        this.deselectEvent.subscribe(this._intervalOnDeselect, this, true);
    };
    
    $W.IntervalCalendar._DEFAULT_CONFIG = YAHOO.widget.CalendarGroup._DEFAULT_CONFIG;
    $L.extend($W.IntervalCalendar, YAHOO.widget.CalendarGroup, {
        _dateString: function(d){
            var a = [];
            a[this.cfg.getProperty($W.IntervalCalendar._DEFAULT_CONFIG.MDY_MONTH_POSITION.key) -
            1] = (d.getMonth() +
            1);
            a[this.cfg.getProperty($W.IntervalCalendar._DEFAULT_CONFIG.MDY_DAY_POSITION.key) -
            1] = d.getDate();
            a[this.cfg.getProperty($W.IntervalCalendar._DEFAULT_CONFIG.MDY_YEAR_POSITION.key) -
            1] = d.getFullYear();
            var s = this.cfg.getProperty($W.IntervalCalendar._DEFAULT_CONFIG.DATE_FIELD_DELIMITER.key);
            return a.join(s);
        },
        _dateIntervalString: function(l, u){
            var s = this.cfg.getProperty($W.IntervalCalendar._DEFAULT_CONFIG.DATE_RANGE_DELIMITER.key);
            return (this._dateString(l) + s +
            this._dateString(u));
        },
        
        getInterval: function(){
            // Get selected dates
            var dates = this.getSelectedDates();
            if (dates.length > 0) {
                // Return lower and upper date in array
                var l = dates[0];
                var u = dates[dates.length - 1];
                return [l, u];
            }
            else {
                // No dates selected, return empty array
                return [];
            }
        },
        
        setInterval: function(d1, d2){
            // Determine lower and upper dates
            var b = (d1 <= d2);
            var l = b ? d1 : d2;
            var u = b ? d2 : d1;
            // Update configuration
            this.cfg.setProperty('selected', this._dateIntervalString(l, u), false);
            this._iState = 2;
        },
        
        resetInterval: function(){
            // Update configuration
            this.cfg.setProperty('selected', [], false);
            this._iState = 0;
        },
        
        _intervalOnBeforeSelect: function(t, a, o){
            // Update interval state
            this._iState = (this._iState + 1) % 3;
            if (this._iState === 0) {
                // If starting over with upcoming selection,
                // first deselect all
                this.deselectAll();
                this._iState++;
            }
        },
        
        _intervalOnSelect: function(t, a, o){
            // Get selected dates
            var dates = this.getSelectedDates();
            if (dates.length > 1) {
                var l = dates[0];
                var u = dates[dates.length - 1];
                this.cfg.setProperty('selected', this._dateIntervalString(l, u), false);
            }
            // Render changes
            this.render();
        },
        
        _intervalOnBeforeDeselect: function(t, a, o){
            if (this._iState !== 0) {
                return false;
            }
        },
        
        _intervalOnDeselect: function(t, a, o){
            if (this._iState !== 0) {
                // If part of an interval is already selected,
                // then first deselect all
                this._iState = 0;
                this.deselectAll();
                
                // Get individual date deselected and page
                // containing it
                var d = a[0][0];
                var date = YAHOO.widget.DateMath.getDate(d[0], d[1] - 1, d[2]);
                var page = this.getCalendarPage(date);
                if (page) {
                    // Now (re)select the individual date
                    page.beforeSelectEvent.fire();
                    this.cfg.setProperty('selected', this._dateString(date), false);
                    page.selectEvent.fire([d]);
                }
                // Swallow up since we called deselectAll above
                return false;
            }
        }
    });
})();
