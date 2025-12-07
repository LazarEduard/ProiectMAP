package Class.Project.CarRental.filter;


    //Test whether an entity matches
public interface AbstractFilter<Entity> {

    //Returns true if the entity matches the filter criteria.
    boolean matches(Entity entity);
}
