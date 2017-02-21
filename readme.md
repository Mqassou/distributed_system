Remote Method Invocation (RMI) est une plateforme qui facilite le d�veloppement d�applications Java r�parties.
Elle permet � du code Java s�ex�cutant dans une JVM d�appeler les m�thodes d�un objet r�sidant dans une autre
JVM, �ventuellement localis�e sur une autre machine. RMI assure ainsi la transparence d�acc�s aux objets :
l�appel de m�thode sur un objet distant se fait (presque) de la m�me mani�re que pour un objet local.


Objectif du projet : 

Il s�agit d�enrichir les fonctionnalit�s de RMI pour permettre le d�veloppement d�applications r�parties
compos�es d�objets r�pliqu�s. Il s�agit notamment d�assurer la transparence de localisation et de r�plication des
objets de l�application. En fonction de leurs besoins, les objets doivent pouvoir choisir entre plusieurs politiques
de r�plication : active, passive, semi-active. Il faut �galement pouvoir identifier les m�thodes qui ne modifient
pas l��tat de l�objet r�pliqu�. La plateforme doit en tirer parti pour diminuer leur dur�e d�ex�cution et r�partir la
charge sur les diff�rentes r�pliques d�un objet.