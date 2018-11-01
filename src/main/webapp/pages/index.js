const $ = require('jquery');
const _ = require('underscore');

const { onLogin, showLogin, showRegister, hideOverlay } = require('../overlay.js');

const showMaps = (user) => {
  $.ajax({
    url: '/ws/map/public',
  }).fail(console.log).done((data) => {
    let tpl = _.template(require('./tpl/public_map.html'));
    data.forEach((map) => {
      $('div.app').append(tpl({map: map}));
    });
  });
};

const main = () => {
  onLogin(showMaps, showRegister);
}

module.exports = {
  main: main
};
