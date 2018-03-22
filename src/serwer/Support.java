package serwer;

public class Support {


	public double distance(double latitudeA, double latitudeB, double longitudeA, double longitudeB) {

		  double a = 0.5 - Math.cos((latitudeB - latitudeA) * Math.PI / 180)/2 + 
				  Math.cos(latitudeA * Math.PI / 180) * Math.cos(latitudeB * Math.PI / 180) * 
		          (1 - Math.cos((longitudeB - longitudeA) * Math.PI / 180))/2;

		  return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
		}

}
