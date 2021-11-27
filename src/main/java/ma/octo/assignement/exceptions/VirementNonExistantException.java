package ma.octo.assignement.exceptions;

public class VirementNonExistantException extends Exception{
    private static final long serialVersionUID = 1L;

    public VirementNonExistantException() {
    }

    public VirementNonExistantException(String message) {
        super(message);
    }
}
