const $ = require('jquery');
const _ = require('underscore');

const { onLogin, showLogin, showRegister, hideOverlay } = require('../overlay.js');

const showProfile = (user) => {
  let tpl = _.template(require('./tpl/profile.html'));
  $('div.app').html(tpl({user: user}));
};

const main = () => {
  onLogin((user) => {
    console.log(user);
    showProfile(user);
  }, showLogin);
};

module.exports = {
  main: main
};
