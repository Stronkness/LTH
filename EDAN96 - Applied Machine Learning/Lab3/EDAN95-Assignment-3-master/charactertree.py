from typing import Optional


class CharacterTree:
    """
    This is a `CharacterTree` where each node can contain a character and an associated probability. Note that these values are
    not set for inner nodes, only for leaf nodes.

    """

    def __init__(self, probability: float, character: Optional[str] = None,
                 left_child: Optional["CharacterTree"] = None,
                 right_child: Optional["CharacterTree"] = None):
        self.character = character
        self.probability = probability
        self.left_child = left_child
        self.right_child = right_child

    def add_left(self, left_child: "CharacterTree"):
        """
        Set the left child to a subtree

        :param left_child: a CharacterTree instance
        :return: nothing
        """
        self.left_child = left_child

    def add_right(self, right_child: "CharacterTree"):
        """
        Set the right child to a subtree

        :param right_child: a CharacterTree instance
        :return: nothing
        """
        self.right_child = right_child

    def is_leaf(self) -> bool:
        """
        Returns true if the tree is a leaf, False otherwise.

        :return: True if the tree is a leaf
        """
        is_leaf = self.left_child is None and self.right_child is None
        if is_leaf:
            assert self.character is not None, "A character should be set for a leaf node."
            assert self.probability is not None, "A probability should be set for a leaf node."
        return is_leaf

    def __str__(self) -> str:
        """
        String representation of the tree.

        :return: String representation of the tree.
        """
        repr = f"(p={self.probability:.2f}"
        if self.character is not None:
            repr += f", character={self.character}"
        repr += f", left={self.left_child}, right={self.right_child})"
        return repr
