const $ = require('jquery');
const queryString = require('query-string');

const { onLogin, showLogin, showRegister, hideOverlay } = require('../overlay.js');

function initMap(map) {

  //la locationlist quiprovient de ES
  var locationList =[
    ['GrandmoulinParis', 48.8299181, 2.3812189999999998, 'tag1'],
    ['Paris', 48.856614,2.3522219000000177, 'tag2']
  ];



  var map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 48.830759, lng: 2.359203999999977},
    zoom: 12,
    mapTypeId: 'roadmap'
  });
  addMarkers(locationList);

  // creer le searchbox et le mettre en haut a gauche
  var input = document.getElementById('pac-input');
  var searchBox = new google.maps.places.SearchBox(input);
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current map's viewport.
  map.addListener('bounds_changed', function() {
    searchBox.setBounds(map.getBounds());
  });

  var markers = [];
  //ajout d'un listener si l'utilisateur selectionne une prediction et récupèration 
  // plus d'info sur cette place 
  searchBox.addListener('places_changed', function() {
    var places = searchBox.getPlaces();

    if (places.length == 0) {
      return;
    }

    //efface les anciens marqueus pour rajouter le nouveau a leurs places
    markers.forEach(function(marker) {
      marker.setMap(null);
    });
    markers = [];
    //pour chaque place on recupère son icone, son nom et sa location
    var bounds = new google.maps.LatLngBounds();
    places.forEach(function(place) {
      if (!place.geometry) {
        console.log("Returned place contains no geometry");
        return;
      }
      var icon = {
        url: place.icon,
        size: new google.maps.Size(71, 71),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(17, 34),
        scaledSize: new google.maps.Size(25, 25)
      };

      // creer un marqueur pour chaque postion
      markers.push(new google.maps.Marker({
        map: map,
        icon: icon,
        title: place.name,
        position: place.geometry.location
      }));

      if (place.geometry.viewport) {
        // Only geocodes have viewport.
        bounds.union(place.geometry.viewport);
      } else {
        bounds.extend(place.geometry.location);
      }
    });
    map.fitBounds(bounds);
  });



  //listener pour ajouter un popup avec le formulaire sur la map
  google.maps.event.addListener(map, 'click', function(event) {
    var myLatLng = event.latLng;
    var lat = myLatLng.lat();
    var lng = myLatLng.lng();
    openFormulaire(lat,lng);
  });


  //lecontenu de infowindow (le formulaire)
  var contentWindow=
    '<form action="myServer" method="post">'+
    '<div>'+
    '<label for="place">Place :</label>'+
    '<input type="text" id="place" name="user_place" >'+
    '</div>'+
    '<div>'+
    '<label for="tag"> #tag :</label>'+
    '<input type="tag" id="tag" name="user_tag">'+
    '</div>'+
    '<button type="button" id="submit" onclick="toSubmit()">submit</button> '+
    '<button type="reset" id="cancel">cancel</button> '+
    '<input type="file">'+'<br>'+
    '<img src="" height="200" alt="Image preview...">'+
    '</form>'
  ;

  var marker;
  function openFormulaire(lat,lng) {
    var infowindow = new google.maps.InfoWindow({
      'position' : {lat:lat, lng:lng}, 
      'content': contentWindow

    });

    marker = new google.maps.Marker({ //on créé le marqueur
      position: {lat:lat, lng:lng},
      map: map
    });
    infowindow.open(map, marker);


    //listener pour pour ouvrir l'infowindow si elle est fermée
    marker.addListener('click',function(){
      infowindow.open(map, marker);

    });
    //listener pour supprimer le marker de la map
    //PS: IL FAUT IMPLEMENTER ICI LA SUPRESSION DES INFO DE LA BD
    marker.addListener('rightclick',function(){
      this.setMap(null);
      marker=null;
    });
  }

  /*	//fonction toSubmit()
  function toSubmit(){
    var tag = document.getElementById("tag").value;
      alert(tag); 
  }
  */



  //ajout sur la map des markeurs aprtir d'une liste de locations provenant de ES
  function addMarkers(locationList){
    var marker, i, infoWindowContent;
    for (var i = 0; i < locationList.length; i++){

      infoWindowContent='<h3>'+'Position: '+'</h3>'+ '<h4>'+locationList[i][0]+'</h4>'+'<br>'+'<h3>'+'#Tag: '+'</h3>'+'<h4>'+locationList[i][3]+'</h4>';
      var infowindow = new google.maps.InfoWindow({
        'position' : {lat:locationList[i][1], lng:locationList[i][2]},
        'content': infoWindowContent
      });
      marker = new google.maps.Marker({
        position: new google.maps.LatLng(locationList[i][1], locationList[i][2]),
        map: map,
        title: locationList[i][0]
      });
      infowindow.open(map, marker);
      //listener pour pour ouvrir l'infowindow si elle est fermée
      marker.addListener('click',function(){
        infowindow.open(map, marker);

      });
      marker.addListener('rightclick',function(){
        this.setMap(null);
        marker=null;
      });

    }
  }
}

const showUserMaps = (user) => {
  $('div.app').html(require('./tpl/create_map.html'));
  let form = $('div.app form.form-create-map');
  form.submit(() => {
    $.ajax({
      method: 'PUT',
      url: '/ws/map/add',
      data: form.serialize(),
    }).always(console.log);
    return false;
  });
  $.ajax({
    url: '/ws/user/maps',
  }).always(console.log);
};

const showMap = (user, map) => {
  $.ajax({
    url: '/ws/map/by-name/' + map
  }).fail(console.log).done((data) => {
    console.log(data);
    $('div.app').html(require('./tpl/map.html'));
    window.gapicb = ((_data) => initMap(_data))(data); // TODO promise https://github.com/BespokeView/Load-Google-API/blob/master/src/index.js
  });
};

const main = () => {
  const parsed = queryString.parse(location.search);
  console.log(parsed);
  if (parsed.id)
    onLogin((user) => showMap(user, parsed.id), showLogin);
  else
    onLogin(showUserMaps, showLogin);
}

module.exports = {
  main: main
};
