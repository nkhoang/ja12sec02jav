Ext.define('practView.event.EventManager', {
  extend     : 'Ext.util.Observable',
  addListener: function (eventName, handler, scope, options) {
    if (scope) {
      scope.on({
        scope        : this,
        beforedestroy: function () { //create closure
          console.debug('before event\'s owner is destroyed');
          this.removeListener(eventName, handler, scope);
        }
      });
    }

    this.superclass.addListener.apply(this, arguments);
  }
});