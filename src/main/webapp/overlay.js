const $ = require('jquery');
const _ = require('underscore');

const showPage = (page) => {
  let tpl = require('./pages/tpl/' + page + '.html');
  $('div.overlay').html(tpl);
  $('div.app').css('display', 'none');
  $('div.overlay').css('display', 'block');
};

const postForm = (form, method, endpoint, callback) => {
  return () => {
    $.ajax({
      method: method,
      data: form.serialize(),
      url: '/ws/' + endpoint,
      dataType: 'json',
    }).done((data) => {
      if (data.success)
        callback(data);
    }).fail((err) => {
      alert('Error');
      console.log(err);
    });
    return false;
  }
};

const logout = () => {
  $.ajax({
    method: 'POST',
    url: '/ws/auth/logout',
  }).always(() => window.location.reload());
  return false;
};

const onLogin = (logged_callback, out_callback) => {
  $.ajax({
    method: 'POST',
    url: '/ws/auth/user',
    dataType: 'json',
  }).done((data) => {
    if (data) {
      $('li.menu-logout').on('click', logout);
      $('li.menu-logout').css('display', 'block');
      logged_callback(data);
    }
    else
      out_callback();
  }).fail((err) => {
    alert('Error');
    console.log(err);
  });
};

const showLogin = () => {
  showPage('login');
  $('div.overlay a.register-link').on('click', () => {
    showRegister();
    return false;
  });
  let form = $('div.overlay form.login-form');
  form.on('submit', postForm(form, 'POST', 'auth/login', () => { window.location.reload(); }));
};

const showRegister = () => {
  showPage('register');
  $('div.overlay a.login-link').on('click', () => {
    showLogin();
    return false;
  });
  let form = $('div.overlay form.register-form');
  form.on('submit', postForm(form, 'PUT', 'auth/signup', showLogin));
};

const hideOverlay = () => {
  $('div.overlay').css('display', 'none');
  $('div.app').css('display', 'block');
};

module.exports = {
  onLogin: onLogin,
  showLogin: showLogin,
  showRegister: showRegister,
  hideOverlay: hideOverlay
};
