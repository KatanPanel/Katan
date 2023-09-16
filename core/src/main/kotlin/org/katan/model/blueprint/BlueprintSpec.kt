package org.katan.model.blueprint

public interface BlueprintSpec {

    public val name: String

    public val version: String

    public val remote: BlueprintSpecRemote?

    public val build: BlueprintSpecBuild?

    public val options: List<BlueprintSpecOption>
}

public interface BlueprintSpecOption {

    public val id: String

    public val name: String

    public val type: List<String>

    public val env: String?

    public val defaultValue: String?
}

public interface BlueprintSpecRemote {

    public val origin: String
}

public interface BlueprintSpecBuild {

    public val image: BlueprintSpecImage

    public val entrypoint: String

    public val env: Map<String, String>

    public val instance: BlueprintSpecInstance?
}

public interface BlueprintSpecInstance {

    public val name: String?
}

public interface BlueprintSpecImage {
    public interface Identifier : BlueprintSpecImage {

        public val id: String
    }

    public interface Ref : BlueprintSpecImage {

        public val ref: String
        public val tag: String
    }

    public interface Multiple : BlueprintSpecImage {

        public val images: List<Ref>
    }
}
