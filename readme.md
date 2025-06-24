# Quizzroom

Quizzroom est une application Java Swing permettant de gérer des buzzers pour des quiz, avec communication via MQTT.

## Fonctionnalités

- Ajouter jusqu'à 15 buzzers dynamiquement.
- Sélectionner les buzzers à activer.
- Envoyer un message MQTT lorsqu'un buzzer est pressé.

## Démarrage

1. Cloner le dépôt et ouvrir le projet dans votre IDE Java.
2. Assurez-vous d'avoir les dépendances MQTT (Paho).
3. Lancez la classe `com.ynov.kiwi.BuzzerApp`.

## Structure du projet

- `BuzzerApp.java` : Point d'entrée de l'application et interface graphique.
- `BuzzerPanel.java` : Composant graphique pour chaque buzzer.
- `MQTTService.java` : Gestion de la connexion et de l'envoi de messages MQTT.

## Configuration MQTT

Les paramètres MQTT sont définis dans le code (`BuzzerPanel.java`). Modifiez-les si besoin pour votre propre broker.

## Auteur

Projet réalisé par Quentin SIRAUD, Nicolas LEPINAY et Kevin LOCATELLI