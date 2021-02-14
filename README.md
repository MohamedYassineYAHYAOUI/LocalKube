# LocalKube
Une application permettant le déploiement et le contrôle d'applications écrite en Java à l'intérieur de conteneurs docker.

LocalKube est composée :

 -d'une application serveur nommée local-kube composée de deux types de services REST :
  
    des services REST permettant de contrôler les applications déployées par un utilisateur
    des services REST permettant la discussion entre LocalKube et les applications déployées
    
 -d'une librairie cliente nommée local-kube-api qui est ajoutée dans chaque conteneur. 

Elle est accessible par chaque application déployée et peut discuter avec les services REST de discussion entre LocalKube et les applications déployées.
