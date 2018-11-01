const $ = require('jquery');

const { onLogin, showLogin, showRegister, hideOverlay } = require('../overlay.js');

const showUserFriends = (user) => {
  $.ajax({
    url: '/ws/user/friends',
  }).always(console.log);
};

const main = () => {
  onLogin(showUserFriends, showLogin);
}

module.exports = {
  main: main
};
