Remote Method Invocation (RMI) est une plateforme qui facilite le développement d’applications Java réparties.
Elle permet à du code Java s’exécutant dans une JVM d’appeler les méthodes d’un objet résidant dans une autre
JVM, éventuellement localisée sur une autre machine. RMI assure ainsi la transparence d’accès aux objets :
l’appel de méthode sur un objet distant se fait (presque) de la même manière que pour un objet local.


Objectif du projet : 

Il s’agit d’enrichir les fonctionnalités de RMI pour permettre le développement d’applications réparties
composées d’objets répliqués. Il s’agit notamment d’assurer la transparence de localisation et de réplication des
objets de l’application. En fonction de leurs besoins, les objets doivent pouvoir choisir entre plusieurs politiques
de réplication : active, passive, semi-active. Il faut également pouvoir identifier les méthodes qui ne modifient
pas l’état de l’objet répliqué. La plateforme doit en tirer parti pour diminuer leur durée d’exécution et répartir la
charge sur les différentes répliques d’un objet.